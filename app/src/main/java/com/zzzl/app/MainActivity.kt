package com.zzzl.app

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zzzl.smartcard.SmartCard
import com.zzzl.smartcard.entity.CommandApdu
import com.zzzl.smartcard.utils.LogUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity: AppCompatActivity() {

    private val TAG = "MainActivity"

    val scope = MainScope()

    private var i: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun executeCommand(view: View) {
        scope.launch {
            val cardResult = SmartCard.execute("00a40400085943542E55534552")
            LogUtil.d(TAG,"====>>${cardResult}")
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
        }
    }
    fun executeCommandWithChannelOpen(view: View) {
        scope.launch {
            i++
            if(i % 3 == 0){
                val r0 = SmartCard.executeWithChannelOpened("00a40400085943542E55534552")
                LogUtil.d(TAG,"====>>${r0}")
            }
            val r1 = SmartCard.executeWithChannelOpened("00a4000002ddf1")
            LogUtil.d(TAG,"====>>${r1}")
            val r2 = SmartCard.executeWithChannelOpened("00b0950058")
            LogUtil.d(TAG,"====>>${r2}")

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
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SmartCard.closeService()
    }
}