package com.example.petapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.petapp.data.local.relations.PetWithVaccinationSchedules
import com.example.petapp.data.model.PetEntity

@Dao
interface PetDAO {
    @Query("SELECT * FROM pet WHERE id = :userid")
    suspend fun getPetById(userid: String): List<PetEntity?>

    /**
     * Lấy thông tin thú cưng cùng với tất cả lịch tiêm chủng của nó.
     *
     * @return LiveData chứa danh sách thú cưng và lịch tiêm của chúng
     */
    @Transaction
    @Query("SELECT * FROM pet")
    fun getPetsWithVaccinationSchedules(): LiveData<List<PetWithVaccinationSchedules>>

    /**
     * Lấy thông tin của một thú cưng cùng với tất cả lịch tiêm chủng của nó.
     *
     * @param petId ID của thú cưng cần tìm
     * @return LiveData chứa thông tin thú cưng và lịch tiêm của nó
     */
    @Transaction
    @Query("SELECT * FROM pet WHERE id = :petId")
    fun getPetWithVaccinationSchedules(petId: Long): LiveData<PetWithVaccinationSchedules>
}