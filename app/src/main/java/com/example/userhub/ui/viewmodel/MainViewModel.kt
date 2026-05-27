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

    private val _sortOrder = MutableLiveData<Int>(0)

    val userResult: LiveData<Result<List<UserEntity>>> = _searchQuery.switchMap { query ->
        _sortOrder.switchMap { sortCode ->
            if (query.isNullOrEmpty()) {
                userRepository.searchAndSortLocalUsers("", sortCode)
            } else {
                userRepository.searchAndSortLocalUsers(query, sortCode)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(sortCode: Int) {
        _sortOrder.value = sortCode
    }

    fun refreshUsers() = userRepository.getUsers()
}