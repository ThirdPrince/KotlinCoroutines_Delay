package org.example.retrofit_sample

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED


fun createApiService(): ApiServiceMock {
    return Proxy.newProxyInstance(
        ApiServiceMock::class.java.classLoader,
        arrayOf(ApiServiceMock::class.java),
        ApiServiceProxy()
    ) as ApiServiceMock
}

class ApiServiceProxy : InvocationHandler {


    override fun invoke(
        proxy: Any,
        method: Method,
        args: Array<out Any>?
    ): Any {
        val getAnnotation =
            method.getAnnotation(GET::class.java)
        if (getAnnotation == null) {
            throw UnsupportedOperationException(
                "Unknown method ${method.name}"
            )
        }


        args?.forEach {
            println("args --> $it")
        }


        /**
         * Kotlin suspend 方法
         *
         * 编译后：
         *
         * fun getUserById(
         *    id:Int,
         *    continuation:Continuation
         * )
         *
         */
        val isSuspend =
            method.parameterTypes
                .lastOrNull() == Continuation::class.java



        println(
            "isSuspend=$isSuspend"
        )


        val request =
            resolvePath(
                getAnnotation.path,
                method,
                args
            )


        println(
            "request path=${request.path}"
        )



        if (isSuspend) {


            val continuation =
                args!!.last()
                        as Continuation<Any>


            /**
             * 模拟网络请求
             */
            Thread {

                Thread.sleep(1000)


                continuation.resume(
                    """
                    GET ${request.path}

                    User id=${request.pathValue}
                    """.trimIndent()
                )


            }.start()

            /**
             * 告诉 Kotlin 协程：
             *
             * 当前函数暂时不返回
             *
             */
            return COROUTINE_SUSPENDED

        } else {
            return """
                GET ${request.path}
                User id=${request.pathValue}
            """.trimIndent()

        }
    }

    private fun resolvePath(
        path: String,
        method: Method,
        args: Array<out Any>?
    ): RequestParm {


        var realPath = path


        var pathName = ""

        var pathValue = ""



        method.parameters
            .forEachIndexed { index, parameter ->


                val annotation =
                    parameter.getAnnotation(
                        Path::class.java
                    )


                if (annotation != null) {


                    pathName =
                        annotation.value



                    if (args != null) {


                        pathValue =
                            args[index].toString()



                        realPath =
                            realPath.replace(
                                "{$pathName}",
                                pathValue
                            )

                    }

                }

            }


        return RequestParm(
            pathName,
            realPath,
            pathValue
        )


    }
}

data class RequestParm(
    val pathName: String,
    val path: String,
    val pathValue: String
)