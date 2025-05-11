package com.example.petapp.data.repository

import com.example.petapp.data.api.ApiService
import com.example.petapp.data.api.CreatePetRequest
import com.example.petapp.data.api.RetrofitClient
import com.example.petapp.data.local.dao.PetDAO
import com.example.petapp.data.model.PetEntity

class PetRepository(private val petDAO: PetDAO) {
    private val apiService: ApiService by lazy {
        RetrofitClient.instance
    }

    suspend fun addPet(petEntity: PetEntity): Long {
        // Insert the pet into the remote database
        val reqAddPet = CreatePetRequest(
            id = petEntity.id,
            name = petEntity.name,
            breed_name = petEntity.breedName,
            birth_date = petEntity.birthDate,
            gender = petEntity.gender,
            color = petEntity.color,
            height = petEntity.height,
            weight = petEntity.weight,
            userid = petEntity.userId
        )
        val response = apiService.createPet(reqAddPet)
        if (!response.isSuccessful) {
            return -1
        }
        return petDAO.createPet(petEntity)
    }

    suspend fun getPetByUserId(userId: String): List<PetEntity> {
        return petDAO.getPetByUserId(userId)
    }

    // get pet by id
    suspend fun getPetById(petId: String): PetEntity? {
        return petDAO.getPetById(petId)
    }

    // get pet id list by user id
    suspend fun getPetIdListByUserId(userId: String): List<String>? {
        return petDAO.getPetIdListByUserId(userId)
    }
}