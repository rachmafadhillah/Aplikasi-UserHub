package com.example.userhub.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
class UserEntity(
    @field:PrimaryKey
    @field:ColumnInfo(name = "id")
    val id: String,

    @field:ColumnInfo(name = "name")
    val name: String,

    @field:ColumnInfo(name = "email")
    val email: String,

    @field:ColumnInfo(name = "city")
    val city: String,

    @field:ColumnInfo(name = "address")
    val address: String,

    @field:ColumnInfo(name = "phoneNumber")
    val phoneNumber: String,

    @field:ColumnInfo(name = "gender")
    val gender: Int,
)