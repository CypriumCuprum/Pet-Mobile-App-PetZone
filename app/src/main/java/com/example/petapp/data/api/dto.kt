package com.example.petapp.data.api

import java.util.Date

data class GPSUpdateRequest(
    val deviceId: String,
    val latitude: Double,
    val longitude: Double,
    val batteryLevel: Int
)


data class CreateGPSRequest(
    val id: String,
    val name: String,
    val latitude: Double = -1.0,
    val longitude: Double = -1.0,
    val status: String = "Offline",
    val battery: Int = -1,
    val image_url: String? = null,
    val petid: String
)

data class CreateGPSResponse(
    val message: String
)

data class UpdateGPSResponse(
    val message: String
)

data class GPSDeviceResponse(
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val battery: String
)

/*
class PetCreate(PetBase):
    id: Optional[str] = None
    breed_name: str
    gender: str
    birth_date: Optional[date] = None
    color: Optional[str] = None
    height: float
    weight: float
    image_url: Optional[str] = None
    userid: str
 */
data class CreatePetRequest(
    val id: String,
    val name: String,
    val breed_name: String,
    val gender: String,
    val birth_date: String? = null,
    val color: String? = null,
    val height: Float,
    val weight: Float,
    val image_url: String? = null,
    val userid: String
)

data class CreatePetResponse(
    val id: String
)

data class RegisterRequest(
    val id: String,
    val fullname: String,
    val username: String,
    val password: String,
)

data class RegisterResponse(
    val id: String,
    val fullname: String,
    val username: String,
)