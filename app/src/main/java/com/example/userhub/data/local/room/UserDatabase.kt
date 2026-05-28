package com.example.userhub.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.userhub.data.local.entity.CityEntity
import com.example.userhub.data.local.entity.UserEntity

@Database(entities = [UserEntity::class, CityEntity::class], version = 2, exportSchema = false) // 💡 1. Naikkan versi jadi 2
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: UserDatabase? = null
        fun getInstance(context: Context): UserDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java, "User.db"
                ).fallbackToDestructiveMigration().build()
            }
    }
}