package com.zzzl.smartcard.entity

data class OpenChannelResult(val status: Int, val message: String) {
    companion object{
        const val OPEN_CHANNEL_STATUS_SUCCESS = 1
        const val OPEN_CHANNEL_STATUS_FAIL = 2
    }
    fun isSuccess(): Boolean{
        return status == OPEN_CHANNEL_STATUS_SUCCESS
    }
}