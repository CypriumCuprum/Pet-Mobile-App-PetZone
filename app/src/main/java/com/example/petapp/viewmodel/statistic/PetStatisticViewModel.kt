package com.example.petapp.viewmodel.statistic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.petapp.data.local.AppDatabase
import com.example.petapp.data.model.PetStatisticEntity
import com.example.petapp.data.model.StatisticTypeEntity
import com.example.petapp.data.repository.PetStatisticRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class PetStatisticViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PetStatisticRepository
    private val allPetStatistics: LiveData<List<PetStatisticEntity>>
    private val _foodStatistics = MutableLiveData<List<PetStatisticEntity>>()
    val foodStatistics: LiveData<List<PetStatisticEntity>> = _foodStatistics
    private val foodStatisticTypeId: UUID = UUID.fromString("11111111-1111-1111-1111-111111111112") // Thay thế với ID thực tế

    init {
        val petStatisticDao = AppDatabase.getInstance(application).petStatisticDAO()
        repository = PetStatisticRepository(petStatisticDao)
        allPetStatistics = repository.allPetStatistics
//        CoroutineScope(Dispatchers.IO).launch {
//            val count = repository.getCount()
//            if (count == 0) {
//                val sampleData = listOf(
//                    PetStatisticEntity(
//                        id = UUID.fromString("11111111-1111-1111-1111-111111111111"),
//                        value = 300f,
//                        recorded_at = Date(),
//                        note = "test1",
//                        statistic_typeid = UUID.fromString("11111111-1111-1111-1111-111111111111"),
//                        petid = UUID.fromString("11111111-1111-1111-1111-111111111111"),
//                    ),
//                    PetStatisticEntity(
//                        id = UUID.fromString("11111111-1111-1111-1111-111111111112"),
//                        value = 350f,
//                        recorded_at = Date(),
//                        note = "test1",
//                        statistic_typeid = UUID.fromString("11111111-1111-1111-1111-111111111111"),
//                        petid = UUID.fromString("11111111-1111-1111-1111-111111111111"),
//                    ),
//                )
//                sampleData.forEach { repository.insert(it) }
//            }
//        }
    }

    fun getAll(): LiveData<List<PetStatisticEntity>> {
        return this.allPetStatistics
    }

    fun insert(petStatistic: PetStatisticEntity) = viewModelScope.launch {
        repository.insert(petStatistic)
    }

    fun update(petStatistic: PetStatisticEntity) = viewModelScope.launch {
        repository.update(petStatistic)
    }

    fun delete(petStatistic: PetStatisticEntity) = viewModelScope.launch {
        repository.delete(petStatistic)
    }

    fun getPetStatistics(petId: String): LiveData<List<PetStatisticEntity>> {
        return repository.getPetStatistics(petId)
    }

    fun getPetStatisticsByTypeAndPet(
        petId: String,
        typeId: UUID
    ): LiveData<List<PetStatisticEntity>> {
        return repository.getPetStatisticByTypeAndPet(petId, typeId)
    }

    fun getPetStatisticsByType(typeId: UUID): LiveData<List<PetStatisticEntity>> {
        return repository.getPetStatisticsByType(typeId)
    }

    suspend fun getPetStatisticById(petStatisticId: UUID): PetStatisticEntity? {
        return repository.getPetStatisticById(petStatisticId)
    }

    suspend fun getLatestPetStatistic(petId: String, statisticTypeId: UUID): PetStatisticEntity? {
        return repository.getLatestPetStatistic(petId, statisticTypeId)
    }
    fun loadFoodStatistics(currentPetId: String?) {
        currentPetId?.let { petId ->
            viewModelScope.launch {
                try {
                    // Lấy dữ liệu thống kê thức ăn từ repository
                    val statistics = repository.getPetStatisticsByTypeAndPetId(
                        petId = petId,
                        statisticTypeId = foodStatisticTypeId
                    )
                    _foodStatistics.postValue(statistics)
                } catch (e: Exception) {
                    // Xử lý lỗi ở đây
                    e.printStackTrace()
                }
            }
        }
    }

}