package com.example.petapp.viewmodel.statistic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.petapp.data.local.AppDatabase
import com.example.petapp.data.model.PetStatisticEntity
import com.example.petapp.data.model.StatisticTypeEntity
import com.example.petapp.data.repository.StatisticTypeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class StatisticTypeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StatisticTypeRepository
    private val allStatisticTypes: LiveData<List<StatisticTypeEntity>>

    init {
        val statisticTypeDao = AppDatabase.getDatabase(application).statisticTypeDAO()
        repository = StatisticTypeRepository(statisticTypeDao)
        allStatisticTypes = repository.allStatisticTypes
        CoroutineScope(Dispatchers.IO).launch {
            val count = repository.getCount()
            if (count == 0) {
                val sampleData = listOf(
                    StatisticTypeEntity(
                        id = UUID.fromString("11111111-1111-1111-1111-111111111111"),
                        name = "Weight",
                        unit = "kg",
                        description = "Weight Tracking",
                    ),
                    StatisticTypeEntity(
                        id = UUID.fromString("11111111-1111-1111-1111-111111111112"),
                        name = "Food",
                        unit = "gram",
                        description = "Food Tracking",
                    ),
                )
                sampleData.forEach { repository.insert(it) }
            }
        }
    }

    fun getAll(): LiveData<List<StatisticTypeEntity>>{
        return this.allStatisticTypes
    }

    fun insert(statisticType: StatisticTypeEntity) = viewModelScope.launch {
        repository.insert(statisticType)
    }

    fun update(statisticType: StatisticTypeEntity) = viewModelScope.launch {
        repository.update(statisticType)
    }

    fun delete(statisticType: StatisticTypeEntity) = viewModelScope.launch {
        repository.delete(statisticType)
    }

    suspend fun getById(typeId: UUID): StatisticTypeEntity? {
        return repository.getStatisticTypeById(typeId)
    }
}