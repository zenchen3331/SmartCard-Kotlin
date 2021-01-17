package com.zzzl.smartcard.entity

data class CommandApdu(val apdu: String, val targetSW: String = "9000") {
}