package com.example.petapp.viewmodel.reminder

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.petapp.data.model.Reminder
import com.example.petapp.data.repository.ReminderRepository

class RemindersViewModel(
    private val repository: ReminderRepository = ReminderRepository()
) : ViewModel() {
    val reminders: LiveData<List<Reminder>> = repository.reminders
}