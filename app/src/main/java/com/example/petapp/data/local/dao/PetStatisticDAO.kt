package com.example.petapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.petapp.data.model.PetStatisticEntity
import java.util.UUID
import androidx.lifecycle.LiveData

@Dao
interface PetStatisticDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stat: PetStatisticEntity)

    @Update
    suspend fun update(petStatistic: PetStatisticEntity)

    @Delete
    suspend fun delete(petStatistic: PetStatisticEntity)

    @Query("SELECT * FROM pet_statistic")
    fun getAll(): LiveData<List<PetStatisticEntity>>

    @Query("SELECT * FROM pet_statistic WHERE id = :id")
    suspend fun getById(id: UUID): PetStatisticEntity?

    @Query("SELECT * FROM pet_statistic WHERE petid = :petId ORDER BY recorded_at DESC")
    fun getByPetId(petId: String): LiveData<List<PetStatisticEntity>>

    @Query("SELECT * FROM pet_statistic WHERE statistic_typeid = :statisticTypeId ORDER BY recorded_at DESC")
    fun getByType(statisticTypeId: UUID): LiveData<List<PetStatisticEntity>>

    @Query("SELECT * FROM pet_statistic WHERE statistic_typeid = :statisticTypeId AND petid= :petId ORDER BY recorded_at DESC")
    fun getByTypeAndPet(petId: String, statisticTypeId: UUID): LiveData<List<PetStatisticEntity>>

    @Query("SELECT COUNT(*) FROM statistic_type")
    suspend fun getCount(): Int

    @Query("""
    SELECT * FROM pet_statistic 
    WHERE petid = :petId AND statistic_typeid = :statisticTypeId
    ORDER BY recorded_at DESC 
    LIMIT 1
""")
    suspend fun getLatestPetStatistic(petId: String, statisticTypeId: UUID): PetStatisticEntity?

    @Query("SELECT * FROM pet_statistic WHERE petid = :petId AND statistic_typeid = :statisticTypeId ORDER BY recorded_at DESC")
    suspend fun getByPetIdAndStatisticType(petId: String, statisticTypeId: UUID): List<PetStatisticEntity>


}
