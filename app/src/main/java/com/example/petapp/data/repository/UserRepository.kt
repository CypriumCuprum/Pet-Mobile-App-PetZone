package com.example.petapp.data.repository

import com.example.petapp.data.local.dao.UserDAO
import com.example.petapp.data.model.UserEntity

class UserRepository(private val dao: UserDAO) {
    suspend fun login(username: String, password: String) =
        dao.login(username, password)

    suspend fun register(user: UserEntity) =
        dao.register(user)
}