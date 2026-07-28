package org.example.flow

import jdk.jfr.internal.OldObjectSample.emit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.log

val ioScope = CoroutineScope(Dispatchers.IO)
fun main() = runBlocking{
    val sample = CallbackFlowSample()
    launch {

        val result = sample.setting.first()
        log("result1= $result")
        val result11 = sample.setting.first()
        log("result11= $result11")
    }

     log("end")


}

class CallbackFlowSample {
    val setting = callbackFlow {
        log("upstream start")

        delay(100)

        trySend(1)

        awaitClose {
            log("awaitClose")
        }
    }.shareIn(ioScope, SharingStarted.Eagerly,0)
}