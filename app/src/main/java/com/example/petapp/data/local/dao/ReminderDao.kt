package com.example.petapp.data.local.dao
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
    suspend fun getAllReminders(): List<ReminderEntity>
}