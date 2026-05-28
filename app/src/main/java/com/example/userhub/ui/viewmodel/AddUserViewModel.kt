package com.example.userhub.ui.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.userhub.data.Result
import com.example.userhub.data.repository.UserRepository
import com.example.userhub.data.response.CityResponseItem
import com.example.userhub.data.response.UserResponseItem

class AddUserViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun addUser(user: UserResponseItem) = userRepository.insertUser(user)

    fun fetchCitiesFromApi(): LiveData<Result<List<CityResponseItem>>> {
        return userRepository.getCitiesRemote()
    }

    fun getUniqueCitiesLocal(): LiveData<List<String>> {
        return userRepository.getCitiesFromCache()
    }
}