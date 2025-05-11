package com.example.petapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.petapp.data.model.PetEntity

@Dao
interface PetDAO {
    @Query("SELECT * FROM pet WHERE id = :id")
    suspend fun getPetById(id: String): PetEntity?

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun createPet(petEntity: PetEntity): Long

    @Query("SELECT * FROM pet WHERE userid = :userId")
    suspend fun getPetByUserId(userId: String): List<PetEntity>

    // get Pet Id List by user id
    @Query("SELECT id FROM pet WHERE userid = :userId")
    suspend fun getPetIdListByUserId(userId: String): List<String>?
}