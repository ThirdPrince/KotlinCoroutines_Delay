package org.example.retrofit_sample

import kotlinx.coroutines.runBlocking
import org.example.log

fun main() = runBlocking {
    val apiService = createApiService()
    val userResponse = apiService.getUserById(4)
    log(userResponse)
}