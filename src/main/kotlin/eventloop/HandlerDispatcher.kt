package org.example.eventloop

import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Delay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Runnable

@OptIn(InternalCoroutinesApi::class)
class HandlerDispatcher(private val handler: Handler) : CoroutineDispatcher(), Delay {

    // 判断是否需要调度。如果当前已经在 Looper 所在的线程，可以不需要调度直接执行
    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return Thread.currentThread() !== handler.mLooper.mThread
    }

    // 核心桥接：协程需要分发执行时，把 block(Runnable) 封装成消息 post 给我们的 Handler
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        handler.post(block)
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        // 1. 把“恢复协程”的动作包装成一个 Runnable
        val block = Runnable {
            // resume 让协程在当前调度器（即主线程）恢复执行
            with(continuation) { resumeUndispatched(Unit) }
        }

        // 2. 将包装好的任务，直接通过你的 Handler 塞进 MessageQueue，并指定延时时间
        val msg = Message().apply {
            callback = block
        }
        handler.sendMessageDelayed(msg, timeMillis)

        // 3. 处理协程被取消的情况：如果延时还没到协程就被 cancel 了，应当把消息从队列移除
        continuation.invokeOnCancellation {
            // 可以在此处实现从 mQueue 中移除该 msg 的逻辑
        }
    }
}