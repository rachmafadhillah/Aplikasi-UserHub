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

    private val _cityFilter = MutableLiveData<String>("")

    val userResult: LiveData<Result<List<UserEntity>>> = _searchQuery.switchMap { query ->
        _sortOrder.switchMap { sortCode ->
            _cityFilter.switchMap { city ->
                userRepository.searchSortAndFilterLocalUsers(query.orEmpty(), sortCode, city)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(sortCode: Int) {
        _sortOrder.value = sortCode
    }

    fun setCityFilter(city: String) {
        _cityFilter.value = city
    }

    val cities: LiveData<List<String>> = userRepository.getUniqueCities()

    fun refreshUsers() = userRepository.getUsers()
}