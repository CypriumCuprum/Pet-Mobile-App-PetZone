package com.example.petapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.petapp.data.model.MedicalReportEntity

@Dao
interface MedicalReportDAO {
    @Query("SELECT * FROM medical_report WHERE petid = :petid")
    suspend fun getMedicalReportByPetId(petid: String): List<MedicalReportEntity>

    // create medical report
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createMedicalReport(medicalReport: MedicalReportEntity): Long

    // delete medical report by id
    @Query("DELETE FROM medical_report WHERE id = :id")
    suspend fun deleteMedicalReport(id: String): Int

    // update medical report by id
    @Query("UPDATE medical_report SET title = :title, hospital = :hospital, veterinarian = :veterinarian, description = :description, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateMedicalReport(
        id: String,
        title: String,
        hospital: String,
        veterinarian: String,
        description: String,
        updatedAt: String
    ): Int
}