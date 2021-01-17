package com.zzzl.smartcard.ext

fun String.isTargetSW(targetSW: String): Boolean{
    return substring(length-4,length) == targetSW
}