package org.example.sample

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import org.example.eventloop.Looper
import org.example.log

fun main() = runBlocking {
    // 使用两个二元信号量实现精准的交替等待与唤醒
    val semEven = Semaphore(1) // 初始为 1，让偶数协程先跑
    val semOdd = Semaphore(1, acquiredPermits = 1)
    var count = 0
    val maxCount = 10

    // 偶数协程
    val job1 = launch {
        while (count <= maxCount) {
            semEven.acquire() // 等待轮到自己
            if (count > maxCount) {
                semOdd.release() // 防止另一个协程死锁在 acquire 上
                break
            }

            log("even --> count: $count")
            count++
            delay(1500) // 模拟耗时

            semOdd.release()  // 释放信号，唤醒奇数协程
        }
    }

    // 奇数协程
    val job2 = launch {
        while (count <= maxCount) {
            semOdd.acquire()  // 等待偶数协程释放信号
            if (count > maxCount) break

            log("odd --> count: $count")
            count++
            delay(1500) // 模拟耗时

            semEven.release() // 释放信号，唤醒偶数协程
        }
    }

    launch {
        while (true){
            log("this Thread can work")
            delay(500)
        }
    }

    job1.join()
    job2.join()
    println("end")
}