package com.zzzl.app

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zzzl.smartcard.SmartCard
import com.zzzl.smartcard.core.ReaderType
import com.zzzl.smartcard.entity.CardResult
import com.zzzl.smartcard.entity.CommandApdu
import com.zzzl.smartcard.utils.LogUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity: AppCompatActivity() {

    private val TAG = "MainActivity"

    private val scope = MainScope()

    private var i: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun executeCommand(view: View) {
        scope.launch {
            val cardResult = SmartCard.execute("00a40400085943542E55534552")
            LogUtil.d(TAG,"====>>${cardResult}")
            Toast.makeText(this@MainActivity, cardResult.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    fun executeCommandList(view: View) {
        scope.launch {

            val list = listOf(
                CommandApdu("00a40400085943542E55534552"),
                CommandApdu("00a4000002ddf1"),
                CommandApdu("00b0950058")
            )
            val rList = SmartCard.execute(list)
            LogUtil.d(TAG,rList.toString())
            Toast.makeText(this@MainActivity, rList.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    fun executeCommandWithChannelOpen(view: View) {
        scope.launch {
            i++
            var r0: CardResult? = null
            if(i % 3 == 0){
                r0 = SmartCard.executeWithChannelOpened("00a40400085943542E55534552")
                LogUtil.d(TAG,"====>>${r0}")
            }
            val r1 = SmartCard.executeWithChannelOpened("00a4000002ddf1")
            LogUtil.d(TAG,"====>>${r1}")
            val r2 = SmartCard.executeWithChannelOpened("00b0950058")
            LogUtil.d(TAG,"====>>${r2}")
            if(r0 == null){
                Toast.makeText(this@MainActivity, r1.toString() +"\n"+r2.toString(), Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@MainActivity,
                    r0.toString() +"\n"+
                        r1.toString() +"\n"+
                            r2.toString(), Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun executeCommandListWithChannelOpen(view: View) {
        scope.launch {

            i++
            LogUtil.d("====>>counter:$i")
            val list = if(i %3 ==0){
                listOf(
                    CommandApdu("00a40400085943542E55534552"),
                    CommandApdu("00a4000002ddf1"),
                    CommandApdu("00b0950058")
                )
            }else{
                listOf(
                    CommandApdu("00a4000002ddf1"),
                    CommandApdu("00b0950058")
                )
            }
            val rList = SmartCard.executeWithChannelOpened(list)
            LogUtil.d(TAG,rList.toString())
            Toast.makeText(this@MainActivity,
                rList.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun closeChannel(v: View){
        SmartCard.closeChannel()
    }

    fun changeReaderType(v: View){
        i++
        if(i % 2 == 0){
            SmartCard.changeReaderType(ReaderType.READER_TYPE_ESE1)
            Toast.makeText(
                this,
                "current readerType: ${ReaderType.READER_TYPE_ESE1}",
                Toast.LENGTH_SHORT
            ).show()
        }else{
            SmartCard.changeReaderType(ReaderType.READER_TYPE_SIM1)
            Toast.makeText(
                this,
                "current readerType: ${ReaderType.READER_TYPE_SIM1}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //退出客户端必须调用
        SmartCard.closeService()
    }
}