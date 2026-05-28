package com.example.userhub.data.retrofit

import com.example.userhub.data.response.CityResponseItem
import com.example.userhub.data.response.UserResponseItem
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("user")
    suspend fun getUser(
    ): List<UserResponseItem>

    @POST("user")
    suspend fun addUser(
        @Body user: UserResponseItem
    ): UserResponseItem

    @GET("city")
    suspend fun getCitiesFromApi(): List<CityResponseItem>
}