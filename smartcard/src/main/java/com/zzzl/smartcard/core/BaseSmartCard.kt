package com.zzzl.smartcard.core

abstract class BaseSmartCard(readerType: ReaderType): ISmartCard{


//    abstract fun openChannel(readerType: ReaderType,onSuccess: () -> Unit, onError: (error: String) -> Unit)


//    fun changeReaderType(readerType: ReaderType,onSuccess: () -> Unit, onError: (error: String) -> Unit){
//        closeChannel()
//        openChannel(readerType,onSuccess,onError)
//    }

}