package com.example.petapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.petapp.data.model.PetEntity

@Dao
interface PetDAO {
    @Query("SELECT * FROM pet WHERE id = :userid")
    suspend fun getPetById(userid: String): List<PetEntity?>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun createPet(petEntity: PetEntity): Long
}