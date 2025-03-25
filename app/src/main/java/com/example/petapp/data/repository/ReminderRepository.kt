package com.example.petapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.petapp.data.model.Reminder

class ReminderRepository {
    private val _reminders = MutableLiveData<List<Reminder>>()
    val reminders: LiveData<List<Reminder>> = _reminders

    init{
        _reminders.value = listOf(
            Reminder(1, "Vet Visit", "Bella", "10:00", "2024-09-01", "Daily", true, "vet"),
            Reminder(2, "Feed", "Bella", "12:00", "2024-09-02", "Daily", true, "feed"),
        )
    }

    fun addReminder(reminder: Reminder){
        val currentList = _reminders.value?.toMutableList() ?: mutableListOf()
        currentList.add(reminder)
        _reminders.value = currentList
    }


}