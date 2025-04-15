package com.example.petapp.viewmodel.pet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petapp.data.model.PetEntity
import com.example.petapp.data.repository.PetRepository
import kotlinx.coroutines.launch
import android.util.Log // Optional: for logging
import androidx.lifecycle.ViewModelProvider

class PetAddingViewModel(private val repository: PetRepository) : ViewModel() {

    // Use PetEntity? to signal success/failure more clearly
    private val _addPetResult = MutableLiveData<PetEntity?>()
    val addPetResult: LiveData<PetEntity?> get() = _addPetResult

    // Specific LiveData for error messages
    private val _addPetError = MutableLiveData<String?>()
    val addPetError: LiveData<String?> get() = _addPetError

    fun addPet(pet: PetEntity) {
        // Basic validation (already performed more thoroughly in Activity, but good practice)
        // Note: userId is assumed valid here as it was checked in Activity
        if (pet.name.isBlank() || pet.breedName.isBlank() || pet.gender.isBlank() || pet.birthDate.isNullOrEmpty() || pet.height <= 0 || pet.weight <= 0) {
            _addPetError.postValue("Invalid pet data received.") // More generic error
            _addPetResult.postValue(null) // Indicate failure
            Log.w("PetAddingViewModel", "Invalid PetEntity passed to addPet: $pet")
            return
        }

        // Clear previous error
        _addPetError.postValue(null)

        viewModelScope.launch {
            try {
                Log.d("PetAddingViewModel", "Attempting to add pet via repository: ${pet.id}")
                // Call repository to add the pet
                val addedPetId =
                    repository.addPet(pet) // Assuming addPet returns the ID or the object itself

                // Check repository result (adjust based on what your repository returns)
                // If repository.addPet inserts and returns the ID (or rowId > 0)
                if (addedPetId > 0) { // Assuming it returns rowId > 0 on success
                    Log.i("PetAddingViewModel", "Pet added successfully with ID: ${pet.id}")
                    _addPetResult.postValue(pet) // Post the successfully added pet object
                } else {
                    Log.e(
                        "PetAddingViewModel",
                        "Repository failed to add pet (returned non-positive ID or null)"
                    )
                    _addPetError.postValue("Failed to save pet to the database.")
                    _addPetResult.postValue(null)
                }
                /* // Alternative: If repository returns the full object or null
                val addedPet = repository.addPet(pet)
                if (addedPet != null) {
                   _addPetResult.postValue(addedPet)
                } else {
                   _addPetError.postValue("Failed to save pet to the database.")
                   _addPetResult.postValue(null)
                }
                */

            } catch (e: Exception) {
                Log.e("PetAddingViewModel", "Exception while adding pet: ${e.message}", e)
                _addPetError.postValue("An error occurred: ${e.message}")
                _addPetResult.postValue(null) // Indicate failure
            }
        }
    }

    class Factory(private val repository: PetRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PetAddingViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PetAddingViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}