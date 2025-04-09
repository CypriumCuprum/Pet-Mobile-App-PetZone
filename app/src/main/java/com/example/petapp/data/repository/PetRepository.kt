package com.example.petapp.data.repository

import com.example.petapp.data.local.dao.PetDAO

class PetRepository(private val petDAO: PetDAO) {
    suspend fun getPetById(userid: String) = petDAO.getPetById(userid)
}