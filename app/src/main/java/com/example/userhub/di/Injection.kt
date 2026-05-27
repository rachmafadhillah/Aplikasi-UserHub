package com.example.userhub.di

import android.content.Context
import com.example.userhub.data.repository.UserRepository
import com.example.userhub.data.local.room.UserDatabase
import com.example.userhub.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val database = UserDatabase.getInstance(context)
        val dao = database.userDao()
        return UserRepository.getInstance(apiService, dao)
    }
}