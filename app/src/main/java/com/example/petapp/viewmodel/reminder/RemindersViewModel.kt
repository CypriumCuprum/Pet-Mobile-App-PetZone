package com.example.petapp.viewmodel.reminder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petapp.data.local.AppDatabase
import com.example.petapp.data.model.Reminder
import com.example.petapp.data.model.ReminderEntity
import com.example.petapp.data.repository.ReminderRepository
import kotlinx.coroutines.launch

class RemindersViewModel(
    private val application: Application
) : AndroidViewModel(application) {
    val repository: ReminderRepository
    val allReminders: LiveData<List<ReminderEntity>>
    init {
        val reminderDao = AppDatabase.getInstance(application).reminderDAO()
        repository = ReminderRepository(reminderDao)
        allReminders = repository.allReminders
    }
    fun insertReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            repository.insertReminder(reminder)
        }
    }
    fun updateReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            repository.updateReminder(reminder)
        }
    }
    suspend fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }
}