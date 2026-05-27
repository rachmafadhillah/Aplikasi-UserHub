package com.example.userhub.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.userhub.data.Result
import com.example.userhub.data.retrofit.ApiService
import com.example.userhub.data.local.entity.UserEntity
import com.example.userhub.data.local.room.UserDao
import kotlinx.coroutines.Dispatchers

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userDao: UserDao
) {

    fun getUsers(): LiveData<Result<List<UserEntity>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)

        try {
            val response = apiService.getUser()

            val userList = response.map { userResponseItem ->
                UserEntity(
                    id = userResponseItem.id,
                    name = userResponseItem.name,
                    email = userResponseItem.email,
                    city = userResponseItem.city,
                    address = userResponseItem.address,
                    phoneNumber = userResponseItem.phoneNumber,
                    gender = userResponseItem.gender
                )
            }

            userDao.deleteAll()
            userDao.insertUsers(userList)

        } catch (e: Exception) {
            Log.e("UserRepository", "getUsers Remote Error: ${e.message}")
            emit(Result.Error(e.message.toString()))
        }

        val localData: LiveData<Result<List<UserEntity>>> = userDao.getUser().map { Result.Success(it) }
        emitSource(localData)
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            apiService: ApiService,
            userDao: UserDao
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userDao)
            }.also { instance = it }
    }
}