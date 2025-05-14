package com.example.petapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.petapp.data.model.MedicalReportEntity
import com.example.petapp.data.model.MedicalReportWithPet

@Dao
interface MedicalReportDAO {
    @Query("SELECT * FROM medical_report WHERE petid = :petid")
    suspend fun getMedicalReportByPetId(petid: String): List<MedicalReportEntity>

    @Query("SELECT * FROM medical_report WHERE id = :id")
    suspend fun getMedicalReportById(id: String?): MedicalReportEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createMedicalReport(medicalReport: MedicalReportEntity): Long

    @Query("DELETE FROM medical_report WHERE id = :id")
    suspend fun deleteMedicalReport(id: String): Int

    @Query("UPDATE medical_report SET title = :title, hospital = :hospital, veterinarian = :veterinarian, description = :description, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateMedicalReport(
        id: String,
        title: String,
        hospital: String,
        veterinarian: String,
        description: String,
        updatedAt: String
    ): Int

    @Query(
        """
        SELECT medical_report.*, pet.name AS pet_name, pet.id AS pet_id
        FROM medical_report
        JOIN pet ON medical_report.petid = pet.id
        WHERE pet.userid = :userId
    """
    )
    suspend fun getAllMedicalReportByUserId(userId: String): List<MedicalReportWithPet>

    @Query(
        """
        SELECT medical_report.*, pet.name AS pet_name, pet.id AS pet_id
        FROM medical_report
        JOIN pet ON medical_report.petid = pet.id
        WHERE pet.userid = :userId AND medical_report.created_at BETWEEN :startDate AND :endDate
        ORDER BY medical_report.created_at DESC
        LIMIT :numItem OFFSET :page
    """
    )
    suspend fun getAllMedicalReportByUserIdWithFilterAndSort(
        userId: String,
        numItem: Int,
        page: Int,
        startDate: String,
        endDate: String
    ): List<MedicalReportWithPet>
}