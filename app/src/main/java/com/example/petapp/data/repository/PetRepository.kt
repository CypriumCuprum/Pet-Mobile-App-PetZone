package com.example.petapp.data.repository

import com.example.petapp.data.local.dao.PetDAO
import com.example.petapp.data.model.PetEntity

class PetRepository(private val petDAO: PetDAO) {
    suspend fun getPetById(userid: String) = petDAO.getPetById(userid)

    suspend fun addPet(petEntity: PetEntity): Long {
        return petDAO.createPet(petEntity)
    }

    suspend fun getPetByUserId(userId: String): List<PetEntity> {
        return petDAO.getPetByUserId(userId)
    }
}