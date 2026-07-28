package org.example.retrofit_sample

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy


object RetrofitMock {
    fun <T> create(
        service: Class<T>
    ): T {
        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf(service),
            ApiServiceProxy()
        ) as T

    }
}




