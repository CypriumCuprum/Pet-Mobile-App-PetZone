package com.example.petapp.data.repository

import androidx.room.Insert
import com.example.petapp.data.local.dao.PetDAO
import com.example.petapp.data.model.PetEntity
import com.example.petapp.data.model.submodel.PetReduceForHome

class PetRepository(private val petDAO: PetDAO) {
    suspend fun getPetById(userid: String) = petDAO.getPetById(userid)

    suspend fun addPet(petEntity: PetEntity): Long {
        return petDAO.createPet(petEntity)
    }

    suspend fun getPetReduceForHome(userId: String): List<PetReduceForHome> {
        return petDAO.getPetReduceForHome(userId)
    }
}