package com.example.petapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.petapp.data.model.PetEntity

@Dao
interface PetDAO {
    @Query("SELECT * FROM pet WHERE id = :userid")
    suspend fun getPetById(userid: String): List<PetEntity?>
}