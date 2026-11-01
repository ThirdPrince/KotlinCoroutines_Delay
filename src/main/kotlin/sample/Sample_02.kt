package org.example.sample

import org.example.eventloop.Handler
import org.example.eventloop.Looper
import org.example.eventloop.Message
import org.example.log
import java.util.concurrent.Semaphore
import javax.swing.plaf.synth.ColorType.MAX_COUNT

fun main() {
    Looper.prepare()

    val handler = object: Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            log("msg: $msg")
        }
    }
    var count = 0
    val semEven: Semaphore = Semaphore(1)

    // semOdd 初始有 0 个许可（奇数线程初始阻塞）
    val semOdd: Semaphore = Semaphore(0)

    val t1 = Thread(Runnable {
        while (count <= MAX_COUNT) {
            try {
                semEven.acquire() // 阻塞当前线程直到获得许可

                log("even --> count: $count")
                count++
                Thread.sleep(500) // 线程阻塞睡眠

                semOdd.release() // 释放许可，唤醒奇数线程
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
        }
    })

    val t2 = Thread(Runnable {
        while (count <= MAX_COUNT) {
            try {
                semOdd.acquire() // 阻塞当前线程直到获得许可
                if (count > MAX_COUNT) break

                log("odd --> count: $count")
                count++
                Thread.sleep(500) // 线程阻塞睡眠

                semEven.release() // 释放许可，唤醒偶数线程
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
        }
    })

    handler.sendMessage(Message().apply {
        what=1
    })
    t1.start()
    t2.start()
    handler.sendMessage(Message().apply {
        what=2
    })
    t1.join()
    t2.join()
    handler.sendMessage(Message().apply {
        what=3
    })

    println("end")
    Looper.loop()
}