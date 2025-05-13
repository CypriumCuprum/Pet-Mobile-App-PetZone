package com.example.petapp.data.repository

import androidx.lifecycle.LiveData
import com.example.petapp.data.local.dao.PetStatisticDAO
import com.example.petapp.data.model.PetStatisticEntity
import java.util.*

class PetStatisticRepository(private val petStatisticDao: PetStatisticDAO) {
    val allPetStatistics: LiveData<List<PetStatisticEntity>> = petStatisticDao.getAll()

    suspend fun insert(petStatistic: PetStatisticEntity) {
        petStatisticDao.insert(petStatistic)
    }

    suspend fun update(petStatistic: PetStatisticEntity) {
        petStatisticDao.update(petStatistic)
    }

    suspend fun delete(petStatistic: PetStatisticEntity) {
        petStatisticDao.delete(petStatistic)
    }

    fun getPetStatistics(petId: String):  LiveData<List<PetStatisticEntity>> {
        return petStatisticDao.getByPetId(petId)
    }

    suspend fun getPetStatisticById(id: UUID): PetStatisticEntity? {
        return petStatisticDao.getById(id)
    }

    fun getPetStatisticsByType(typeId: UUID): LiveData<List<PetStatisticEntity>> {
        return petStatisticDao.getByType(typeId)
    }

    fun getPetStatisticByTypeAndPet(petId: String, typeId: UUID): LiveData<List<PetStatisticEntity>> {
        return petStatisticDao.getByTypeAndPet(petId, typeId)
    }

    suspend fun getCount(): Int {
        return petStatisticDao.getCount()
    }

    suspend fun getLatestPetStatistic(petId: String, statisticTypeId: UUID): PetStatisticEntity? {
        return petStatisticDao.getLatestPetStatistic(petId, statisticTypeId)
    }

    suspend fun getPetStatisticsByTypeAndPetId(petId: String, statisticTypeId: UUID): List<PetStatisticEntity> {
        return petStatisticDao.getByPetIdAndStatisticType(petId, statisticTypeId)
    }
}