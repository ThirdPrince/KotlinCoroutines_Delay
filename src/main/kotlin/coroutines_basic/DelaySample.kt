package org.example.coroutines_basic

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.eventloop.Handler
import org.example.eventloop.HandlerDispatcher
import org.example.eventloop.Looper
import org.example.eventloop.Message
import org.example.log
import kotlin.concurrent.thread

val mainScope = CoroutineScope(Job() + Dispatchers.Main)
fun main() {
    println("【Main Thread】${Thread.currentThread().name} 启动...")

    // 1. 为主线程准备 Looper
    Looper.prepare()

    val mainHandler = Handler()

    // 3. 核心：将我们的 Handler 包装内化为协程的 Dispatcher
    val myMainDispatcher = HandlerDispatcher(mainHandler)

    // 4. 定义你的 mainScope (等价于 CoroutineScope(Job() + Dispatchers.Main))
    val mainScope = CoroutineScope(Job() + myMainDispatcher)

    mainScope.launch {
        withContext(Dispatchers.IO) {
            log("start")
        }
        delay(1000)
        log("end")
    }
    // 4. 主线程启动死循环监听
    Looper.loop()
}