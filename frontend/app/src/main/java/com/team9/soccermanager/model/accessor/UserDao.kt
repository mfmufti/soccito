package com.team9.soccermanager.model.accessor

import com.team9.soccermanager.model.User

interface UserDao {
    suspend fun getUserById(id: String) : User?
}