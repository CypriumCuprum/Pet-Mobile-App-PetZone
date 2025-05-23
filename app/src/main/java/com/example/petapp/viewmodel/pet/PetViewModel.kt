// PetViewModel.kt
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

    suspend fun getPetsForHome(userId: String): List<PetEntity> {
        return repository.getPetByUserId(userId)
    }

    //get pet id list by user id
    suspend fun getPetIdListByUserId(userId: String): List<String>? {
        return repository.getPetIdListByUserId(userId)
    }

    // get pet by id
    suspend fun getPetById(petId: String): PetEntity? {
        return repository.getPetById(petId)
    }

    // get repository
    fun getRepository(): PetRepository {
        return repository
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