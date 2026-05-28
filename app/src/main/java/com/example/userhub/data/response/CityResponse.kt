package com.example.userhub.data.response

import com.google.gson.annotations.SerializedName

data class CityResponse(

	@field:SerializedName("CityResponse")
	val cityResponse: List<CityResponseItem>
)

data class CityResponseItem(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String
)
