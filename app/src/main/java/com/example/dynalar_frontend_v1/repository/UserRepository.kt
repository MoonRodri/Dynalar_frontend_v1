package com.example.dynalar_frontend_v1.repository

import User
import com.example.dynalar_frontend_v1.network.RetrofitClient

class UserRepository {
    private val userApiService = RetrofitClient.userApiService;

    suspend fun getAllUsers(): List<User>{
        return userApiService.getAllUsers();
    }

    suspend fun login(user: User): User{
        return userApiService.login(user);
    }
}