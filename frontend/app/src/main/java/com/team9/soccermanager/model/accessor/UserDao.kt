package com.team9.soccermanager.model.accessor

import com.team9.soccermanager.model.User

interface UserDao {
    suspend fun getUserById(id: String) : User?
    suspend fun updateUser(user: User): Boolean
    suspend fun createUser(name: String, email: String): User?
}