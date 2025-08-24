package com.example.gencidevapp.repository

import android.util.Log
import com.example.gencidevapp.data.database.UserDao
import com.example.gencidevapp.data.model.UserModel
import com.example.gencidevapp.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach

class UserRepository(
    private val userDao: UserDao,
    private val apiService: ApiService
) {
    private val TAG = "UserRepository"

    fun getAllUsers(): Flow<List<UserModel>> {
        Log.d(TAG, "getAllUsers called (from DB)")
        return userDao.getAllUsers()
            .onEach { users ->
                Log.d(TAG, "DB emitted ${users.size} users for getAllUsers.")
                if (users.isNotEmpty()) {
                    Log.d(TAG, "First user from DB (getAllUsers): ${users.firstOrNull()}")
                }
            }
            .catch { e ->
                Log.e(TAG, "Error in getAllUsers from DB: ${e.message}", e)
            }
    }

    fun searchUsers(query: String): Flow<List<UserModel>> {
        Log.d(TAG, "searchUsers called with query: '$query' (from DB)")
        val searchQuery = if (query.isNotEmpty()) "%$query%" else "%"
        return userDao.searchUsers(searchQuery)
            .onEach { users ->
                Log.d(TAG, "DB emitted ${users.size} users for searchUsers query: '$query'.")
                if (users.isNotEmpty()) {
                    Log.d(TAG, "First user from DB (searchUsers for '$query'): ${users.firstOrNull()}")
                }
            }
            .catch { e ->
                Log.e(TAG, "Error in searchUsers from DB for query '$query': ${e.message}", e)
            }
    }

    suspend fun refreshUsers() {
        Log.d(TAG, "refreshUsers called - attempting to fetch from API")
        try {
            Log.d(TAG, "Calling apiService.getUsers()")
            val remoteUsers = apiService.getUsers()
            Log.d(TAG, "API returned ${remoteUsers.size} users.")

            if (remoteUsers.isNotEmpty()) {
                Log.d(TAG, "First remote user from API: ${remoteUsers.firstOrNull()}")
                Log.d(TAG, "Attempting to insert ${remoteUsers.size} users into DB.")
                userDao.insertUsers(remoteUsers)
                Log.d(TAG, "${remoteUsers.size} users successfully inserted/updated in DB.")
            } else {
                Log.d(TAG, "API returned an empty list of users. No users inserted into DB.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to refresh users from API: ${e.message}", e)
        }
    }

    suspend fun deleteUser(user: UserModel) {
        Log.d(TAG, "deleteUser called for user ID: ${user.id}, Name: ${user.name}")
        try {
            userDao.deleteUser(user)
            Log.d(TAG, "User deleted from DB, ID: ${user.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete user from DB: ${e.message}", e)
        }
    }
}

