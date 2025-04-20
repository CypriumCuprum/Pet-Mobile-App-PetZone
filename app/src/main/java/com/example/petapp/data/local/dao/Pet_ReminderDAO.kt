package com.example.petapp.data.local.dao
import androidx.room.*
import com.example.petapp.data.model.Pet_ReminderEntity

@Dao
interface Pet_ReminderDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPetReminder(petReminder: Pet_ReminderEntity)

    @Update
    suspend fun updatePetReminder(petReminder: Pet_ReminderEntity)

    @Delete
    suspend fun deletePetReminder(petReminder: Pet_ReminderEntity)

    @Query("SELECT * FROM pet_reminder WHERE petid = :petId AND reminderid = :reminderId")
    suspend fun getPetReminderById(petId: String, reminderId: String): Pet_ReminderEntity?

    @Query("SELECT * FROM pet_reminder")
    suspend fun getAll(): List<Pet_ReminderEntity>
}