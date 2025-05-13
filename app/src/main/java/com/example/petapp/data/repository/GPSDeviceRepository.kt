package com.example.petapp.data.repository

import com.example.petapp.data.api.ApiService
import com.example.petapp.data.api.CreateGPSRequest
import com.example.petapp.data.api.GPSDeviceResponse
import com.example.petapp.data.api.RetrofitClient
import com.example.petapp.data.local.dao.GPSDeviceDAO
import com.example.petapp.data.model.GPSEntity


class GPSDeviceRepository(private val gpsDeviceDAO: GPSDeviceDAO) {
    private val apiService: ApiService by lazy {
        RetrofitClient.instance
    }

    suspend fun insertGPSDevice(gpsDevice: GPSEntity): Long {
        // Insert the GPS device into the remote database
        val request = CreateGPSRequest(
            id = gpsDevice.id,
            name = gpsDevice.name,
            latitude = gpsDevice.latitude,
            longitude = gpsDevice.longitude,
            status = gpsDevice.status,
            battery = gpsDevice.battery,
            image_url = gpsDevice.imageUrl,
            petid = gpsDevice.petId
        )
        try {
            val response = apiService.createGPSDevice(request)
            if (!response.isSuccessful) {
                println("Error creating GPS device: ${response.errorBody()}")
                return -1
            }
        } catch (e: Exception) {
            println("Error creating GPS device: ${e.message}")
            return -1
        }
//        val response = apiService.createGPSDevice(request)
//        if (!response.isSuccessful) {
//            return -1
//        }
        // Insert the GPS device into the local database
        return gpsDeviceDAO.insertGPSDevice(gpsDevice)
    }

    suspend fun getAllGPSDevices(petId: String): List<GPSEntity> {
        return gpsDeviceDAO.getAllGPSDevices(petId)
    }

    // get gps device infor by id through api
    suspend fun getGPSDeviceInfobyAPI(id: String): GPSDeviceResponse {
        val response = apiService.getGPSDevice(id)
        if (response.isSuccessful) {
            // save to local database
            val gpsDevice = response.body()
            if (gpsDevice != null) {
                gpsDeviceDAO.updateGPSDeviceStatus(
                    id,
                    gpsDevice.latitude,
                    gpsDevice.longitude,
                    gpsDevice.status,
                    gpsDevice.battery.toInt()
                )
            }
            return response.body() ?: GPSDeviceResponse(0.0, 0.0, "Offline", "0%")
        } else {
            println("Error: ${response.errorBody()}")
        }
        return GPSDeviceResponse(0.0, 0.0, "Offline", "0%")
    }

    // delete gps device by id
    suspend fun deleteGPSDevice(id: String): Int {
        return gpsDeviceDAO.deleteGPSDevice(id)
    }

    suspend fun updateGPSDevice(
        id: String,
        name: String,
        latitude: Double = -1.0,
        longitude: Double = -1.0,
        status: String = "Offline",
        battery: String = "0%",
        imageUrl: String?
    ): Int {
        return gpsDeviceDAO.updateGPSDevice(
            id,
            name,
            latitude,
            longitude,
            status,
            battery,
            imageUrl
        )
    }
}