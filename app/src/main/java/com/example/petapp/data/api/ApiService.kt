package com.example.petapp.data.api

import com.example.petapp.data.model.ItemTypeEntity
import retrofit2.http.GET

interface ApiService {
    @GET("item_type/get")
    suspend fun getItemTypes(): List<ItemTypeEntity>
}
