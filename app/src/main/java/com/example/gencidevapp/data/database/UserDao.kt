package com.example.gencidevapp.data.database

import androidx.room.*
import com.example.gencidevapp.data.model.UserModel
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserModel>>

    @Query("SELECT * FROM users WHERE name LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%'")
    fun searchUsers(query: String): Flow<List<UserModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserModel>)

    @Delete
    suspend fun deleteUser(user: UserModel)

    @Query("DELETE FROM users")
    suspend fun clearAll()
}