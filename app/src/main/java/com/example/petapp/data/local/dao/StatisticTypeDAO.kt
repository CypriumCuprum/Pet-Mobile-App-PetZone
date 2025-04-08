package com.example.petapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.lifecycle.liveData
import com.example.petapp.data.model.StatisticTypeEntity
import java.util.UUID

@Dao
interface StatisticTypeDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stat: StatisticTypeEntity)

    @Update
    suspend fun update(statisticType: StatisticTypeEntity)

    @Delete
    suspend fun delete(statisticType: StatisticTypeEntity)

    @Query("SELECT * FROM statistic_type WHERE id = :id")
    suspend fun getStatisticTypeById(id: UUID): StatisticTypeEntity?

    @Query("SELECT * FROM statistic_type")
    fun getAll(): LiveData<List<StatisticTypeEntity>>

    @Query("SELECT COUNT(*) FROM statistic_type")
    suspend fun getCount(): Int

}