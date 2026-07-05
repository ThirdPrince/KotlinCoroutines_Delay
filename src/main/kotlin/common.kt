package org.example

/**
 * 优雅的线程敏感日志扩展函数
 * 用法: "你的日志内容".log() 或者 valResult.log("前缀说明")
 */
fun Any?.log(tag: String = "APP_LOG") {
    val threadName = Thread.currentThread().name
    // 格式化输出
    println("[$tag]: $threadName]")
}