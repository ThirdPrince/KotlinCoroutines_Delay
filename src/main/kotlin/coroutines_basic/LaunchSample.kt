package org.example.coroutines_basic

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.log

val defaultScope = CoroutineScope(Dispatchers.Default)
fun main() = runBlocking{
//    log("start")
//    test()
//    log("end")

    launch(Dispatchers.IO) {
        repeat(1000) {
            delay(1000)
            log("it $it")
        }
    }

    log("end")
}

suspend fun test() = coroutineScope {
    launch {
        delay(1000L)
    }

    launch {
        delay(1000L)
    }

    launch {
        delay(1000L)
    }

    defaultScope.launch {
        delay(3000L)
    }
}