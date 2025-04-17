package com.example.petapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.petapp.data.local.dao.ReminderDao
import com.example.petapp.data.model.Reminder
import com.example.petapp.data.model.ReminderEntity

class ReminderRepository(private val reminderDAO: ReminderDao) {
    private val _reminders = MutableLiveData<List<Reminder>>()
    val reminders: LiveData<List<Reminder>> = _reminders
    val allReminders: LiveData<List<ReminderEntity>> = reminderDAO.getAllReminders()

    init{
        _reminders.value = listOf(
            Reminder(1, "Vet Visit", "Bella", "10:00", "2024-09-01", "Daily", true, "vet"),
            Reminder(2, "Feed", "Bella", "12:00", "2024-09-02", "Daily", true, "feed"),
        )
    }

    suspend fun insertReminder(reminder: ReminderEntity){
        reminderDAO.insertReminder(reminder)
    }
    suspend fun updateReminder(reminder: ReminderEntity){
        reminderDAO.updateReminder(reminder)
    }
    suspend fun deleteReminder(reminder: ReminderEntity){
        reminderDAO.deleteReminder(reminder)
    }
    suspend fun getReminderById(reminderId: String): ReminderEntity? {
        return reminderDAO.getReminderById(reminderId)
    }

    fun addReminder(reminder: Reminder){
        val currentList = _reminders.value?.toMutableList() ?: mutableListOf()
        currentList.add(reminder)
        _reminders.value = currentList
    }


}