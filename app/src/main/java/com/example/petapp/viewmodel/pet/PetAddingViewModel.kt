package com.example.petapp.viewmodel.pet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petapp.data.model.PetEntity
import com.example.petapp.data.repository.PetRepository
import kotlinx.coroutines.launch

class PetAddingViewModel(private val repository: PetRepository) : ViewModel() {
    private val _addPetResult = MutableLiveData<PetEntity?>()
    val addPetResult: LiveData<PetEntity?> get() = _addPetResult

    // Bạn có thể giữ LiveData này nếu cần thông báo lỗi cụ thể hơn
    private val _addPetError = MutableLiveData<String?>()
    val addPetError: LiveData<String?> = _addPetError

    fun addPet(pet: PetEntity) {
        // 1. Validate Input (Client-side basic validation)
        if (pet.name.isBlank() || pet.breedName.isBlank() || pet.gender.isBlank() || pet.height.isNaN() || pet.weight.isNaN()) {
            _addPetError.value = "Vui lòng điền đầy đủ tên và loại thú cưng."
            return
        }

        // 2. Bắt đầu xử lý
        _addPetError.value = null // Xóa lỗi cũ

        viewModelScope.launch {
            try {
                // 3. Thêm thú cưng vào cơ sở dữ liệu
                val addedPet = repository.addPet(pet)
                if (addedPet != null) {
                    _addPetResult.postValue(addedPet) // Gửi cả đối tượng Pet khi thành công
                    _addPetError.postValue(null) // Xóa lỗi cũ (nếu có)
                } else {
                    _addPetResult.postValue(null) // Gửi null khi thất bại
                    _addPetError.postValue("Thêm thú cưng không thành công.")
                }
            } catch (e: Exception) {
                _addPetResult.postValue(null)
                _addPetError.postValue("Đã xảy ra lỗi: ${e.message}")
            }
        }
    }

}