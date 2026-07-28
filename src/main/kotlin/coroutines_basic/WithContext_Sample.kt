package org.example.coroutines_basic

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.example.log

val dispatcher = newFixedThreadPoolContext(2, "MyDispatcher")
fun main() = runBlocking {

    test1()
    delay(1000)
    test2()
}

suspend fun test1() = withContext(Dispatchers.IO){
    delay(1000)
    log("test1")
}

suspend fun test2() = withContext(dispatcher){
    delay(1000)
    log("test2")
}