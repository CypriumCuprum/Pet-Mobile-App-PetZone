package com.example.petapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.petapp.data.model.MedicalReportEntity

@Dao
interface MedicalReportDAO {
    @Query("SELECT * FROM medical_report WHERE petid = :petid")
    suspend fun getMedicalReportByPetId(petid: String): List<MedicalReportEntity?>
}