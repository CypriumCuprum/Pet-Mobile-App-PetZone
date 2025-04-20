package com.example.petapp.data.local.dao
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.petapp.data.model.ReminderEntity


@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)


    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("SELECT * FROM reminder WHERE id = :reminderId")
    suspend fun getReminderById(reminderId: String): ReminderEntity?

    @Query("SELECT * FROM reminder")
    fun getAllReminders(): LiveData<List<ReminderEntity>>

    @Query("SELECT DISTINCT reminder.*" +
            "    FROM reminder" +
            "    JOIN pet_reminder ON reminder.id = pet_reminder.reminderid" +
            "    JOIN pet ON pet.id = pet_reminder.petid" +
            "    WHERE pet.userid = :userId")
    fun getRemindersByUserId(userId: String): LiveData<List<ReminderEntity>>
}