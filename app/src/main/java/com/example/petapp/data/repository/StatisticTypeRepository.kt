package com.example.petapp.data.repository

import androidx.lifecycle.LiveData
import com.example.petapp.data.local.dao.StatisticTypeDAO
import com.example.petapp.data.model.StatisticTypeEntity
import java.util.*

class StatisticTypeRepository(private val statisticTypeDao: StatisticTypeDAO) {

    val allStatisticTypes: LiveData<List<StatisticTypeEntity>> = statisticTypeDao.getAll()

    suspend fun insert(statisticType: StatisticTypeEntity) {
        statisticTypeDao.insert(statisticType)
    }

    suspend fun update(statisticType: StatisticTypeEntity) {
        statisticTypeDao.update(statisticType)
    }

    suspend fun delete(statisticType: StatisticTypeEntity) {
        statisticTypeDao.delete(statisticType)
    }

    suspend fun getStatisticTypeById(id: UUID): StatisticTypeEntity? {
        return statisticTypeDao.getStatisticTypeById(id)
    }

    suspend fun getCount(): Int {
        return statisticTypeDao.getCount()
    }
}