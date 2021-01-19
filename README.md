# SmartCard-Kotlin
<p align="center">
   <a href="https://jitpack.io/#zenchen3331/SmartCard-Kotlin">
    <img src="https://jitpack.io/v/zenchen3331/SmartCard-Kotlin.svg" alt="JitPack" />
  </a>
  <a href="https://img.shields.io/github/license/zenchen3331/SmartCard-Kotlin">
    <img src="https://img.shields.io/github/license/zenchen3331/SmartCard-Kotlin?color=brightgreen" alt="License" />
  </a>
  <a href="https://developer.android.com/about/versions/android-4.4.html">
    <img src="https://img.shields.io/badge/API-19+-blue.svg" alt="Min Sdk Version" />
  </a>
  <a href="http://www.apache.org/licenses/LICENSE-2.0">
    <img src="http://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square" alt="License" />
  </a>
</p>
# SmartCard

智能卡访问库（kotlin 版）

#### OpenMobileAPI是提供手机客户端程序访问内置eSE或SIM完成Android设备SE空间管理和卡应用管理的API，Android P以前是通过Simalliance组织提供的API，手机厂商内置到手机系统中实现，Android P以后，谷歌将此API的实现纳入官方API中。

- [Simalliance官方地址](https://simalliance.org/)
- [Google OMA Api官方地址](https://developer.android.google.cn/reference/android/se/omapi/package-summary?hl=en)

SmartCard库是对OpenMobileAPI进行封装，方便快速集成访问手机SE，同时根据系统不同适配以上两种API，使用此库前，需确认当前客户端程序有访问SE的权限，一般需要手机厂商或者SIM卡厂商提供授权。

# 使用介绍
添加依赖

Step 1：Add it in your root build.gradle at the end of repositories:

```
allprojects {
    repositories {
	...
	maven { url 'https://jitpack.io' }
    }
}
```

Step 2：Add the dependency

```
dependencies {
    implementation 'com.github.zenchen3331:SmartCard-Kotlin:0.3.2'
}
```

Step 3：代码调用（必须在协程中调用）

1. 设置Reader类型，默认为SIM1

```
SmartCard.changeReaderType(ReaderType.READER_TYPE_ESE1)
```

2. 执行APDU指令
```
val cardResult = SmartCard.execute("00a40400085943542E55534552")
```
3. 关闭SE通道和服务

```
//执行完成指令调用（如果你使用的是 execute() 则不需要这一步）
SmartCard.closeChannel()
//退出客户端必须调用
SmartCard.closeService()
```

结束，调用就是这么简单，主要是需要确认客户端程序对SE的访问权限，与打包所用的签名有关。

#  API 列表

| 方法名                                                       | 方法描述     | 是否自动关闭通道 |
| ------------------------------------------------------------ | ------------ | ---------------- |
| execute(command: String, targetSW: String)                   | 执行单一指令 | 是               |
| execute(commands: List<CommandApdu>): List<CardResult>       | 执行指令集合 | 是               |
| executeWithChannelOpened(command: String, targetSW: String): CardResult | 执行单一指令 | 否               |
| executeWithChannelOpened(commands: List<CommandApdu>): List<CardResult> | 执行指令集合 | 否               |

## 调用示例

按钮点击，依次调用上述四个方法。

```kotlin
package com.zzzl.app

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zzzl.smartcard.SmartCard
import com.zzzl.smartcard.core.ReaderType
import com.zzzl.smartcard.entity.CommandApdu
import com.zzzl.smartcard.utils.LogUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.random.Random

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
                val r0 = 	 		SmartCard.executeWithChannelOpened("00a40400085943542E55534552")
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
```

# APK

[点击下载](https://github.com/zenchen3331/SmartCard-Kotlin/raw/master/app-debug.apk)

# [WiKi](https://github.com/zenchen3331/SmartCard-Kotlin/wiki)

欢迎发邮件或者提issue

# 感谢
感谢 [Hank](https://github.com/hankfighting) 参考了一些代码和文件

# LICENSE

```
Copyright 2020 zzzl

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
