package com.example.userhub.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.userhub.data.local.entity.CityEntity
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
    AND (:cityFilter = '' OR city = :cityFilter)
    ORDER BY 
        CASE WHEN :sortCode = 1 THEN name END ASC,
        CASE WHEN :sortCode = 2 THEN name END DESC,
        CASE WHEN :sortCode = 0 THEN id END ASC
    """)
    fun searchSortAndFilterUsers(searchQuery: String, sortCode: Int, cityFilter: String): LiveData<List<UserEntity>>

    @Query("SELECT name FROM city_cache ORDER BY name ASC")
    fun getCachedCities(): LiveData<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cities: List<CityEntity>)

    @Query("DELETE FROM city_cache")
    suspend fun deleteAllCities()

}