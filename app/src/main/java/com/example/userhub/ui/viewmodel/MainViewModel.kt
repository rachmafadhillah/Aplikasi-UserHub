package com.example.userhub.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.userhub.data.Result
import com.example.userhub.data.local.entity.UserEntity
import com.example.userhub.data.repository.UserRepository

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _searchQuery = MutableLiveData<String>()


    val userResult: LiveData<Result<List<UserEntity>>> = _searchQuery.switchMap { query ->
        if (query.isNullOrEmpty()) {
            userRepository.getUsers()
        } else {
            userRepository.searchLocalUsers(query)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}