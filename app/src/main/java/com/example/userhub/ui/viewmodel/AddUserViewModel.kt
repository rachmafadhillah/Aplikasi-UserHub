package com.example.userhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.userhub.data.repository.UserRepository
import com.example.userhub.data.response.UserResponseItem

class AddUserViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun addUser(user: UserResponseItem) = userRepository.insertUser(user)
}