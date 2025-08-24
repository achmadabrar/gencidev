package com.example.gencidevapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gencidevapp.data.model.UserModel
import com.example.gencidevapp.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val users: StateFlow<List<UserModel>> = searchQuery
        .onEach { query -> Log.d("UserViewModel", "Search query changed: $query") }
        .flatMapLatest { query ->
            Log.d("UserViewModel", "flatMapLatest triggered with query: '$query'")
            if (query.isEmpty()) {
                Log.d("UserViewModel", "Calling repository.getAllUsers()")
                repository.getAllUsers()
            } else {
                Log.d("UserViewModel", "Calling repository.searchUsers(query: $query)")
                repository.searchUsers(query)
            }
        }
        .onStart { Log.d("UserViewModel", "Users flow collection started") }
        .onEach { userList ->
            Log.d("UserViewModel", "Users flow emitted. Count: ${userList.size}")
            if (userList.isNotEmpty()) {
                Log.d("UserViewModel", "First user in list: ${userList.firstOrNull()}")
            } else {
                Log.d("UserViewModel", "User list is empty.")
            }
        }
        .catch { e -> Log.e("UserViewModel", "Error in users flow: ${e.message}", e) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        Log.d("UserViewModel", "ViewModel initialized.")
        refreshUsers()
    }

    fun updateSearchQuery(query: String) {
        Log.d("UserViewModel", "updateSearchQuery called with: $query")
        _searchQuery.value = query
    }

    fun refreshUsers() {
        Log.d("UserViewModel", "refreshUsers called")
        viewModelScope.launch {
            try {
                repository.refreshUsers()
                Log.d("UserViewModel", "repository.refreshUsers() completed")
                _searchQuery.value = ""
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error calling repository.refreshUsers(): ${e.message}", e)
            }
        }
    }

    fun deleteUser(user: UserModel) {
        Log.d("UserViewModel", "deleteUser called for user: ${user.id}")
        viewModelScope.launch {
            try {
                repository.deleteUser(user)
                Log.d("UserViewModel", "repository.deleteUser() completed for user: ${user.id}")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error calling repository.deleteUser(): ${e.message}", e)
            }
        }
    }
}

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
