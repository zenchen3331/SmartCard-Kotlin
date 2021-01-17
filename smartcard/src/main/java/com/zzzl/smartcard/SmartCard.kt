package com.zzzl.smartcard

import android.os.Build
import com.zzzl.smartcard.core.*
import com.zzzl.smartcard.entity.CardResult
import com.zzzl.smartcard.entity.CommandApdu
import com.zzzl.smartcard.utils.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SmartCard: ISmartCard{

    private val smartCard: ISmartCard by lazy {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            SEServiceGoogle()
        }else{
            SEServiceSIMalliance()
        }
    }

    override suspend fun execute(command: String, targetSW: String): CardResult {
        val cardResult: CardResult
        withContext(Dispatchers.IO){
            cardResult = smartCard.execute(command,targetSW)
        }
        return cardResult
    }

    override suspend fun execute(commands: List<CommandApdu>): List<CardResult> {
        val cardResult: List<CardResult>
        withContext(Dispatchers.IO){
            cardResult = smartCard.execute(commands)
        }
        return cardResult
    }

    override suspend fun executeWithChannelOpened(command: String, targetSW: String): CardResult {
        val cardResult: CardResult
        withContext(Dispatchers.IO){
            cardResult = smartCard.executeWithChannelOpened(command,targetSW)
        }
        return cardResult
    }

    override suspend fun executeWithChannelOpened(commands: List<CommandApdu>): List<CardResult> {
        val cardResult: List<CardResult>
        withContext(Dispatchers.IO){
            cardResult = smartCard.executeWithChannelOpened(commands)
        }
        return cardResult
    }

    override fun closeChannel() {
        smartCard.closeChannel()
    }

    override fun closeService() {
        smartCard.closeService()
    }

    override fun changeReaderType(readerType: ReaderType) {
        smartCard.changeReaderType(readerType)
    }

}