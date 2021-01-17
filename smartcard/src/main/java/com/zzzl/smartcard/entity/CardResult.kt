package com.zzzl.smartcard.entity

data class CardResult(val rapdu: String, val status: Int, val message: String) {
    companion object{
        const val RESULT_SUCCESS = 1
        const val RESULT_NOT_TARGET_SW = 2
        const val RESULT_FAIL = 3
    }
    fun isSuccess(): Boolean{
        return status == RESULT_SUCCESS
    }
}