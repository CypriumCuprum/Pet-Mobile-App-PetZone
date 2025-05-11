package com.example.petapp.data.api

import com.example.petapp.data.model.ItemTypeEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    // Existing endpoint
    @GET("item_type/get")
    suspend fun getItemTypes(): List<ItemTypeEntity>

    // GPS Device endpoints

    // Create a new GPS device
    @POST("gps")
    suspend fun createGPSDevice(@Body gpsDevice: CreateGPSRequest): Response<CreateGPSResponse>

    // Get GPS device information
    @GET("gps/{device_id}")
    suspend fun getGPSDevice(@Path("device_id") deviceId: String): Response<GPSDeviceResponse>

    @POST("pet/create")
    suspend fun createPet(@Body pet: CreatePetRequest): Response<CreatePetResponse>

    @POST("user/register")
    suspend fun registerUser(@Body user: RegisterRequest): Response<RegisterResponse>
}

