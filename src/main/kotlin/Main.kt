package org.example

import org.example.eventloop.Handler
import org.example.eventloop.Looper
import org.example.eventloop.Message
import kotlin.concurrent.thread

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    println("【Main Thread】${Thread.currentThread().name} 启动...")

    // 1. 为主线程准备 Looper
    Looper.prepare()

    // 2. 初始化 Handler 并在内部覆写 handleMessage
    val mainHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            println("【Main Thread】收到消息 -> what: ${msg.what}, 运行在线程: ${Thread.currentThread().name}, 当前时间戳: ${System.currentTimeMillis()}")
        }
    }

    // 3. 启动一个子线程（使用 Kotlin 的 thread 快捷函数）
    thread(name = "Worker-Thread-1") {
        try {
            println("【Worker Thread】${Thread.currentThread().name} 开始工作...")

            // 发送一个即时消息
            val msg1 = Message().apply { what = 100 }
            println("【Worker Thread】发送即时消息(what=100) 时间: ${System.currentTimeMillis()}")
            mainHandler.sendMessage(msg1)

            // 发送一个延时 2 秒的消息
            val msg2 = Message().apply { what = 200 }
            println("【Worker Thread】发送延时2秒消息(what=200) 时间: ${System.currentTimeMillis()}")
            mainHandler.sendMessageDelayed(msg2, 2000)

            // 通过 post 发送一个 Lambda 闭包
            mainHandler.post {
                println("【Main Thread】执行了通过 Handler.post 传过来的 Runnable，当前线程: ${Thread.currentThread().name}")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 4. 主线程启动死循环监听
    Looper.loop()
}