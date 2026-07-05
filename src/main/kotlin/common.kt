package org.example

import kotlin.time.Clock.System.now

/**
 * 优雅的线程敏感日志扩展函数
 * 用法: "你的日志内容".log() 或者 valResult.log("前缀说明")
 */
fun log(vararg msg:Any?) = println("${now()} [${Thread.currentThread().name}] ${msg.joinToString(" ")}")
