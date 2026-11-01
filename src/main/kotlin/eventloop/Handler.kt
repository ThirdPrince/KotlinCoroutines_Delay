package org.example.eventloop

import org.example.log

open class Handler {
    val mLooper: Looper
    private val mQueue: MessageQueue

    // 默认构造函数：绑定当前线程的 Looper
    constructor() {
        mLooper = Looper.myLooper() ?: throw RuntimeException(
            "Can't create handler inside thread that has not called Looper.prepare()"
        )
        mQueue = mLooper.mQueue
    }


    // 发送即时消息
    fun sendMessage(msg: Message): Boolean {
        return sendMessageDelayed(msg, 0)
    }

    // 发送延时消息
    fun sendMessageDelayed(msg: Message, delayMillis: Long): Boolean {
        val delay = if (delayMillis < 0) 0L else delayMillis
        return sendMessageAtTime(msg, System.currentTimeMillis() + delay)
    }

    fun sendMessageAtTime(msg: Message, uptimeMillis: Long): Boolean {
        log("msg=$msg uptimeMillis=$uptimeMillis")
        msg.target = this // 绑定自己
        return mQueue.enqueueMessage(msg, uptimeMillis)
    }

    // 投递 闭包/Lambda 任务 (Kotlin 的特色高阶函数)
    fun post(r: Runnable): Boolean {
        val m = Message().apply {
            callback = r
        }
        return sendMessage(m)
    }

    // 核心分发逻辑
    fun dispatchMessage(msg: Message) {
        if (msg.callback != null) {
            msg.callback?.run()
        } else {
            handleMessage(msg)
        }
    }

    // 留给子类实现或覆写
    open fun handleMessage(msg: Message) {}
}