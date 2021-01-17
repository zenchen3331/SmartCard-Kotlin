package com.zzzl.smartcard.core

interface OpenMobile {

    companion object{
        const val READER_TYPE_SIM = "SIM"
        const val READER_TYPE_ESE = "eSE"
    }

    fun bindService()

    fun getServiceStatus(): Boolean

    fun closeSession()

    fun closeChannelAndSession()

    fun getCurrentAvailableReader(): Any // 用 Any 因为 SIMalliance 和 Google 提供的 Reader 不是一个类
}