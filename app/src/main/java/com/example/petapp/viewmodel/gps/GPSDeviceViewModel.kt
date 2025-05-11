package com.example.petapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.petapp.data.api.GPSDeviceResponse
import com.example.petapp.data.local.AppDatabase
import com.example.petapp.data.model.GPSEntity
import com.example.petapp.data.repository.GPSDeviceRepository
import com.example.petapp.viewmodel.pet.PetViewModel

class GPSDeviceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GPSDeviceRepository

    init {
        val gpsDeviceDao = AppDatabase.getInstance(application).gpsDeviceDAO()
        repository = GPSDeviceRepository(gpsDeviceDao)
    }

    suspend fun getGPSDevicesForPet(petId: String): List<GPSEntity> {
        return repository.getAllGPSDevices(petId)
    }

    suspend fun addGPSDevice(gpsDevice: GPSEntity): Long {
        return repository.insertGPSDevice(gpsDevice)
    }

    suspend fun getGPSDeviceInfoByAPI(id: String): GPSDeviceResponse {
        return repository.getGPSDeviceInfobyAPI(id)
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
        return repository.updateGPSDevice(
            id, name, latitude, longitude, status, battery, imageUrl
        )
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GPSDeviceViewModel::class.java)) {
                return GPSDeviceViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}