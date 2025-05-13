package com.example.petapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.petapp.data.model.ImageMedicalReportEntity

@Dao
interface ImageMedicalReportDAO {
    @Query("SELECT * FROM image_medical_report WHERE medical_reportid = :medicalReportId")
    suspend fun getImageMedicalReportByMedicalReportId(medicalReportId: String): List<ImageMedicalReportEntity>

    // create image medical report
    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun createImageMedicalReport(imageMedicalReport: ImageMedicalReportEntity): Long

    // delete all images medical report by id
    @Query("DELETE FROM image_medical_report WHERE medical_reportid = :medicalReportId")
    suspend fun deleteImageMedicalReportByMedicalReportId(medicalReportId: String): Int

    // delete image medical report by id
    @Query("DELETE FROM image_medical_report WHERE id = :id")
    suspend fun deleteImageMedicalReport(id: String): Int
}