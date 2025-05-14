package com.example.petapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.petapp.data.model.ImageMedicalReportEntity

@Dao
interface ImageMedicalReportDAO {
    @Query("SELECT * FROM image_medical_report WHERE medical_reportid = :medicalReportId")
    suspend fun getImageMedicalReportByMedicalReportId(medicalReportId: String): List<ImageMedicalReportEntity>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun createImageMedicalReport(imageMedicalReport: ImageMedicalReportEntity): Long

    @Query("DELETE FROM image_medical_report WHERE medical_reportid = :medicalReportId")
    suspend fun deleteImageMedicalReportByMedicalReportId(medicalReportId: String): Int

    @Query("DELETE FROM image_medical_report WHERE id = :id")
    suspend fun deleteImageMedicalReport(id: String): Int
}