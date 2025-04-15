package com.example.petapp.viewmodel.pet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.petapp.data.local.AppDatabase
import com.example.petapp.data.model.PetEntity
import com.example.petapp.data.repository.PetRepository

class PetViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PetRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = PetRepository(db.petDao())
    }


    suspend fun addPet(pet: PetEntity) {
        repository.addPet(pet)
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PetViewModel::class.java)) {
                return PetViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}