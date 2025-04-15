package com.example.petapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.petapp.data.model.PetEntity
import com.example.petapp.data.model.submodel.PetReduceForHome

@Dao
interface PetDAO {
    @Query("SELECT * FROM pet WHERE id = :userid")
    suspend fun getPetById(userid: String): List<PetEntity>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun createPet(petEntity: PetEntity): Long

    @Query("SELECT id, name, image_url FROM pet WHERE userid = :userId")
    suspend fun getPetReduceForHome(userId: String): List<PetReduceForHome>
}