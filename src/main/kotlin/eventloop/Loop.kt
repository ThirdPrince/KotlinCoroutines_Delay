package org.example.eventloop

class Looper private constructor() {
    val mQueue: MessageQueue = MessageQueue()
    val mThread: Thread = Thread.currentThread()

    companion object {
        // 使用 Kotlin 的 ThreadLocal 扩展
        private val sThreadLocal = ThreadLocal<Looper>()

        // 准备当前线程的 Looper
        fun prepare() {
            if (sThreadLocal.get() != null) {
                throw RuntimeException("Only one Looper may be created per thread")
            }
            sThreadLocal.set(Looper())
        }

        // 获取当前线程的 Looper
        fun myLooper(): Looper? = sThreadLocal.get()

        // 核心死循环：提取并分发消息
        fun loop() {
            val me = myLooper() ?: throw RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.")
            val queue = me.mQueue

            while (true) {
                val msg = queue.next() ?: return // 收到 quit 信号时 next() 返回 null，退出死循环

                // 分发消息给发送它的那个 Handler
                msg.target?.dispatchMessage(msg)
            }
        }
    }

    fun quit() {
        mQueue.quit()
    }
}