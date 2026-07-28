package org.example.retrofit_sample

interface ApiServiceMock {

    @GET("user/{id}")
    suspend fun getUserById(
        @Path("id") id: Int
    ): String

}