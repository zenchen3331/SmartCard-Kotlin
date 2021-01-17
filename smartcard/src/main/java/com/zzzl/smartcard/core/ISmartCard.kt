package com.zzzl.smartcard.core

import com.zzzl.smartcard.entity.CardResult
import com.zzzl.smartcard.entity.CommandApdu

interface ISmartCard {

    suspend fun execute(command: String,targetSW: String = "9000"): CardResult
    suspend fun executeWithChannelOpened(command: String,targetSW: String = "9000"): CardResult

    suspend fun execute(commands: List<CommandApdu>): List<CardResult>
    suspend fun executeWithChannelOpened(commands: List<CommandApdu>): List<CardResult>

    fun closeChannel()

    fun closeService()

    fun changeReaderType(readerType: ReaderType)

}