package com.example.petapp.data.repository

import com.example.petapp.data.api.ApiService
import com.example.petapp.data.api.RegisterRequest
import com.example.petapp.data.api.RetrofitClient
import com.example.petapp.data.local.dao.UserDAO
import com.example.petapp.data.model.UserEntity

class UserRepository(private val dao: UserDAO) {
    private val apiService: ApiService by lazy {
        RetrofitClient.instance
    }

    suspend fun login(username: String, password: String) =
        dao.login(username, password)

    suspend fun register(user: UserEntity): Long {
        // Insert the user into the remote database
        val req_add_user = RegisterRequest(
            id = user.id,
            username = user.username,
            password = user.password,
            fullname = user.fullname,
        )
        try {
            val response = apiService.registerUser(req_add_user)
            if (!response.isSuccessful) {
                return -1
            }
        } catch (e: Exception) {
            if (user.username == "admin") {
                dao.register(user)
            }
            // Handle the exception if needed
            e.printStackTrace()
        }
//        val response = apiService.registerUser(req_add_user)
//        if (!response.isSuccessful) {
//
//            return -1
//        }
        return dao.register(user)
    }


    suspend fun getUserByUsername(username: String) =
        dao.getUserByUsername(username)
}