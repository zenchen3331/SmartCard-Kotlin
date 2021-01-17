package com.zzzl.smartcard.ext

import com.zzzl.smartcard.core.OpenMobile
import org.simalliance.openmobileapi.Reader
import org.simalliance.openmobileapi.SEService

fun SEService.getReaderSIM1(): Reader {
    return getReader(1,OpenMobile.READER_TYPE_SIM,this)
}

fun SEService.getReaderSIM2(): Reader {
    return getReader(2,OpenMobile.READER_TYPE_SIM,this)
}

fun SEService.getReaderESE1(): Reader {
    return getReader(1,OpenMobile.READER_TYPE_ESE,this)
}

fun SEService.getReaderESE2(): Reader {
    return getReader(2,OpenMobile.READER_TYPE_ESE,this)
}

fun getReader(index: Int,readerType: String ,seService: SEService): Reader{
    var count = 0
    seService.readers.forEach {
        if(it.name.startsWith(readerType)){
            if(index == 1){
                return it
            }
            if(index == 2){
                count++
            }
            if(count > 1){
                return it
            }
        }
    }
    throw InterruptedException("${readerType}${index} select error")
}


fun android.se.omapi.SEService.getReaderSIM1(): android.se.omapi.Reader {
    return getReader(1,OpenMobile.READER_TYPE_SIM,this)
}

fun android.se.omapi.SEService.getReaderSIM2(): android.se.omapi.Reader {
    return getReader(2,OpenMobile.READER_TYPE_SIM,this)
}

fun android.se.omapi.SEService.getReaderESE1(): android.se.omapi.Reader {
    return getReader(1,OpenMobile.READER_TYPE_ESE,this)
}

fun android.se.omapi.SEService.getReaderESE2(): android.se.omapi.Reader {
    return getReader(2,OpenMobile.READER_TYPE_ESE,this)
}

fun getReader(index: Int,readerType: String ,seService: android.se.omapi.SEService): android.se.omapi.Reader{
    var count = 0
    seService.readers.forEach {
        if(it.name.startsWith(readerType)){
            if(index == 1){
                return it
            }
            if(index == 2){
                count++
            }
            if(count > 1){
                return it
            }
        }
    }
    throw InterruptedException("${readerType}${index} select error")
}



