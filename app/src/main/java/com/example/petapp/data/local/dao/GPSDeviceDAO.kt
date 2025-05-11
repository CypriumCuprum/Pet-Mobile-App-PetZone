package com.example.petapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.petapp.data.model.GPSEntity

@Dao
interface GPSDeviceDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGPSDevice(gpsDevice: GPSEntity): Long

    // get all gps devices by pet id
    @Query("SELECT * FROM gps_device WHERE petid = :petId")
    suspend fun getAllGPSDevices(petId: String): List<GPSEntity>

    // update gps device by id
    @Query("UPDATE gps_device SET name = :name, latitude = :latitude, longitude = :longitude, status = :status, battery = :battery, image_url = :imageUrl WHERE id = :id")
    suspend fun updateGPSDevice(
        id: String,
        name: String,
        latitude: Double = -1.0,
        longitude: Double = -1.0,
        status: String = "Offline",
        battery: String = "0%",
        imageUrl: String?
    ): Int

    @Query("UPDATE gps_device SET latitude = :latitude, longitude = :longitude, status = :status, battery = :battery WHERE id = :id")
    suspend fun updateGPSDeviceStatus(
        id: String,
        latitude: Double = -1.0,
        longitude: Double = -1.0,
        status: String = "Offline",
        battery: Int = -1,
    ): Int
}