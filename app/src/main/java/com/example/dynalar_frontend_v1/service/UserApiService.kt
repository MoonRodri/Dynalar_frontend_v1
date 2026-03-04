package com.example.dynalar_frontend_v1.service

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import User

interface UserApiService {
    @GET("user/all")
    suspend fun getAllUsers(): List<User>

    @POST("user/login")
    suspend fun login(@Body user: User): User
}