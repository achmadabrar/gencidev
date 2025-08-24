package com.example.gencidevapp.network

import com.example.gencidevapp.data.model.UserModel
import retrofit2.http.GET

interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<UserModel>
}