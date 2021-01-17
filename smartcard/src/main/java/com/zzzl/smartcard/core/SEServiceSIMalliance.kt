package com.zzzl.smartcard.core

import com.zzzl.smartcard.entity.CardResult
import com.zzzl.smartcard.entity.CommandApdu
import com.zzzl.smartcard.entity.OpenChannelResult
import com.zzzl.smartcard.ext.*
import com.zzzl.smartcard.utils.Hex
import com.zzzl.smartcard.utils.LogUtil
import com.zzzl.smartcard.utils.Utils
import org.simalliance.openmobileapi.Channel
import org.simalliance.openmobileapi.Reader
import org.simalliance.openmobileapi.SEService
import org.simalliance.openmobileapi.Session

class SEServiceSIMalliance(private var readerType: ReaderType = ReaderType.READER_TYPE_SIM1) : BaseSmartCard(readerType),
    OpenMobile, SEService.CallBack {

    private val TAG = "SEServiceSIMalliance"

    private var mSEService: SEService? = null
    private var mChannel: Channel? = null
    private var mSession: Session? = null

    private val mLock = Object()

    override fun serviceConnected(p0: SEService?) {
        synchronized(mLock){
            if(p0?.isConnected!!){
                LogUtil.d("thread notifyAll")
                mLock.notifyAll()
            }
        }
        LogUtil.d(TAG,"success: seService Connected")
    }

    override fun closeChannel() {
        mChannel?.apply {
            if (!isClosed) {
                close()
                LogUtil.d("channel closed:${isClosed}")
            }
        }
    }

    override fun closeService() {
        closeChannelAndSession()
        mSEService?.apply {
            shutdown()
        }
        mSEService = null
    }

    override fun changeReaderType(readerType: ReaderType) {
        this.readerType = readerType
        mChannel = null
    }

    override suspend fun execute(command: String,targetSW: String): CardResult {
        return doExecuteApduCommandAlsoChannel(command,targetSW,true)
    }

    private fun doExecuteApduCommandAlsoChannel(command: String,targetSW: String,isCloseChannel: Boolean): CardResult{
        val r = executeApduCommand(command,targetSW)
        if(isCloseChannel){
            closeChannel()
        }
        return r
    }

    private fun executeApduCommand(command: String,targetSW: String): CardResult {
        bindService()
        kotlin.runCatching {
            if (command.isEmpty() || command.length < 6) {
                return CardResult(
                    "",
                    CardResult.RESULT_FAIL,
                    "command is null or length is not enough"
                )
            }
            LogUtil.e(TAG, "Command APDU:$command")
            // 选择 aid 打开通道
            if ("00A404".equals(command.substring(0, 6), true)) {
                closeChannelAndSession()
                // 获取 AID
                val totalLength = command.substring(8, 10).toInt(16)
                val aid = command.substring(10, 10 + totalLength * 2)
                val openChannelResult = openCurrentChannel(aid)
                if (openChannelResult.isSuccess()) {
                    val rapdu = Hex.bytesToHexString(mChannel?.selectResponse)
                    LogUtil.d(TAG, "Response APDU:$rapdu")
                    return if(rapdu.isTargetSW(targetSW)){
                        CardResult(rapdu, CardResult.RESULT_SUCCESS, openChannelResult.message)
                    }else{
                        CardResult(rapdu, CardResult.RESULT_NOT_TARGET_SW, openChannelResult.message)
                    }
                }
                return CardResult("", CardResult.RESULT_FAIL, openChannelResult.message)
            }
            if(mChannel == null){
                throw InterruptedException("please open channel first")
            }
            val byteCommand = Hex.hexStringToBytes(command)
            mChannel?.apply {
                val byteRapdu = mChannel?.transmit(byteCommand)
                val rapdu = Hex.bytesToHexString(byteRapdu)
                LogUtil.d(TAG, "Response APDU:$rapdu")
                return CardResult(rapdu, CardResult.RESULT_SUCCESS, "apdu transmit success")
            }
        }.onFailure {
            return CardResult("", CardResult.RESULT_FAIL, it.message ?: "message is null")
        }
        return CardResult("", CardResult.RESULT_FAIL, "something has error")
    }

    private fun openCurrentChannel(aid: String): OpenChannelResult {
        val reader = getCurrentAvailableReader()
        // 判断通道是否可用
        if (!reader.isSecureElementPresent) {
            return OpenChannelResult(
                OpenChannelResult.OPEN_CHANNEL_STATUS_FAIL,
                "selected reader can not use"
            )
        }
        closeSession()
        mSession = reader.openSession()
        val byteAid = Hex.hexStringToBytes(aid)
        LogUtil.d(TAG, "open channel applet: $aid")
        if (mSession != null) {
            mChannel = mSession!!.openLogicalChannel(byteAid)
        }
        if (mChannel == null) {
            return OpenChannelResult(OpenChannelResult.OPEN_CHANNEL_STATUS_FAIL, "channel is null")
        }
        return OpenChannelResult(
            OpenChannelResult.OPEN_CHANNEL_STATUS_SUCCESS,
            "open channel success"
        )
    }

    override suspend fun execute(commands: List<CommandApdu>): List<CardResult>{
        return doExecuteApduCommandsAlsoChannel(commands,true)
    }

    private fun doExecuteApduCommandsAlsoChannel(commands: List<CommandApdu>,isCloseChannel: Boolean): List<CardResult>{
        val results = ArrayList<CardResult>()
        commands.forEach {
            val r = executeApduCommand(it.apdu,it.targetSW)
            results.add(r)
            if(!r.isSuccess()){
                if(isCloseChannel){
                    closeChannel()
                }
                return results
            }
        }
        if(isCloseChannel){
            closeChannel()
        }
        return results
    }


    override suspend fun executeWithChannelOpened(command: String, targetSW: String): CardResult {
        return doExecuteApduCommandAlsoChannel(command,targetSW,false)
    }

    override suspend fun executeWithChannelOpened(commands: List<CommandApdu>): List<CardResult> {
        return doExecuteApduCommandsAlsoChannel(commands,false)
    }

    override fun bindService() {
        if(mSEService == null){
            mSEService = SEService(Utils.getApp().applicationContext, this)
            if(!mSEService?.isConnected!!){
                synchronized(mLock){
                    LogUtil.d("thread is waiting")
                    mLock.wait()
                }
            }
        }

    }

    override fun getServiceStatus(): Boolean {
        return mSEService?.isConnected ?: false
    }

    override fun closeSession() {
        mSession?.apply {
            if (!isClosed) {
                close()
            }
        }
    }

    override fun closeChannelAndSession() {
        closeChannel()
        closeSession()
    }

    override fun getCurrentAvailableReader(): Reader {
        LogUtil.d(TAG, "select reader name:$readerType")
        when (readerType) {
            ReaderType.READER_TYPE_SIM1 -> {
                return mSEService?.getReaderSIM1()!!
            }
            ReaderType.READER_TYPE_SIM2 -> {
                return mSEService?.getReaderSIM2()!!
            }
            // 谨慎选择 eSE，没有权限可能会阻塞程序。
            // Choose eSE carefully, as no permission may block the program.
            ReaderType.READER_TYPE_ESE1 -> {
                return mSEService?.getReaderESE1()!!
            }
            ReaderType.READER_TYPE_ESE2 -> {
                return mSEService?.getReaderESE2()!!
            }
            else -> {
                throw InterruptedException("readerType error, please check your ReaderType select")
            }
        }
    }
}