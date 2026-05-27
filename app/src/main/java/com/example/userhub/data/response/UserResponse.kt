package com.example.userhub.data.response

import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("UserResponse")
	val userResponse: List<UserResponseItem>
)

data class UserResponseItem(

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("phoneNumber")
	val phoneNumber: String,

	@field:SerializedName("gender")
	val gender: Int,

	@field:SerializedName("city")
	val city: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("email")
	val email: String
)
