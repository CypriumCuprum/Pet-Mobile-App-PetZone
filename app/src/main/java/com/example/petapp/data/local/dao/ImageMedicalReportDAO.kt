package com.example.petapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.petapp.data.model.ImageMedicalReportEntity

@Dao
interface ImageMedicalReportDAO {
    @Query("SELECT * FROM image_medical_report WHERE medical_reportid = :medicalReportId")
    suspend fun getImageMedicalReportByMedicalReportId(medicalReportId: String): List<ImageMedicalReportEntity?>
}