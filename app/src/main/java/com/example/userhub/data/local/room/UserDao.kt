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

    @Query("SELECT * FROM user WHERE name LIKE :searchQuery")
    fun searchUsers(searchQuery: String): LiveData<List<UserEntity>>

    @Query("""
    SELECT * FROM user 
    WHERE name LIKE :searchQuery 
    ORDER BY 
        CASE WHEN :sortCode = 1 THEN name END ASC,
        CASE WHEN :sortCode = 2 THEN name END DESC,
        CASE WHEN :sortCode = 0 THEN id END ASC
    """)
    fun searchAndSortUsers(searchQuery: String, sortCode: Int): LiveData<List<UserEntity>>
}