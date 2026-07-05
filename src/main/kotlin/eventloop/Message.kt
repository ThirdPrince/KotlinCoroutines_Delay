package org.example.eventloop

class Message {
    var what: Int = 0
    var arg1: Int = 0
    var obj: Any? = null

    // 关键：执行的时间戳（毫秒）
    var whenTime: Long = 0L
    // 关键：指向下一个消息，链表结构
    var next: Message? = null
    // 关键：由哪个 Handler 发出，就由哪个 Handler 处理
    var target: Handler? = null
    // 允许传入绑定的任务
    var callback: Runnable? = null

    override fun toString(): String {
        return "Message(what=$what)"
    }
}