package org.example.eventloop

class MessageQueue {
    // 消息队列头节点
    private var mMessages: Message? = null
    private var mQuitting = false

    // 存入消息（按时间单链表升序插入）
    fun enqueueMessage(msg: Message, whenTime: Long): Boolean {
        synchronized(this) {
            if (mQuitting) return false

            msg.whenTime = whenTime
            val p = mMessages

            // 如果队列为空，或者新消息的执行时间比头节点还早，直接插到头部
            if (p == null || whenTime == 0L || whenTime < p.whenTime) {
                msg.next = p
                mMessages = msg
                (this as Object).notify() // 唤醒在 next() 中 wait 的线程
            } else {
                // 否则，遍历链表，按时间顺序插入到合适的位置
                var prev: Message = p
                var current: Message? = p.next
                while (current != null && whenTime >= current.whenTime) {
                    prev = current
                    current = current.next
                }
                msg.next = current
                prev.next = msg
            }
        }
        return true
    }

    // 取出消息（核心阻塞循环）
    fun next(): Message? {
        while (true) {
            synchronized(this) {
                if (mQuitting) return null

                val now = System.currentTimeMillis()
                val msg = mMessages

                if (msg != null) {
                    if (now < msg.whenTime) {
                        // 时间还没到，计算需要等多久，然后 wait 阻塞
                        try {
                            (this as Object).wait(msg.whenTime - now)
                        } catch (e: InterruptedException) {
                            // 被打断则继续循环
                        }
                    } else {
                        // 时间到了，弹出头节点返回
                        mMessages = msg.next
                        msg.next = null
                        return msg
                    }
                } else {
                    // 队列里没消息，死等直到被 notify 唤醒
                    try {
                        (this as Object).wait()
                    } catch (e: InterruptedException) {
                        // 继续
                    }
                }
            }
        }
    }

    fun quit() {
        synchronized(this) {
            mQuitting = true
            (this as Object).notifyAll()
        }
    }
}