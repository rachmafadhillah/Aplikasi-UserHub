package com.example.userhub.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.userhub.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getUser(): LiveData<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("DELETE FROM user")
    suspend fun deleteAll()
}