package com.example.userhub.ui

import androidx.lifecycle.ViewModel
import com.example.userhub.data.repository.UserRepository

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getUsers() = userRepository.getUsers()
}