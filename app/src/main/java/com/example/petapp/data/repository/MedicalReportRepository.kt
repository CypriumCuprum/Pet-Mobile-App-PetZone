package com.example.petapp.data.repository

import com.example.petapp.data.local.dao.ImageMedicalReportDAO
import com.example.petapp.data.local.dao.MedicalReportDAO
import com.example.petapp.data.model.ImageMedicalReportEntity
import com.example.petapp.data.model.MedicalReportEntity

class MedicalReportRepository(
    private val medicalReportDAO: MedicalReportDAO,
    private val imageMedicalReportDAO: ImageMedicalReportDAO
) {
    suspend fun getAllMedicalReportsByPetId(petId: String): List<MedicalReportEntity> {
        return medicalReportDAO.getMedicalReportByPetId(petId)
    }

    suspend fun getAllImageMedicalReportByMedicalReportId(medicalReportId: String): List<ImageMedicalReportEntity> {
        return imageMedicalReportDAO.getImageMedicalReportByMedicalReportId(medicalReportId)
    }

    suspend fun insertMedicalReport(medicalReport: MedicalReportEntity): Long {
        return medicalReportDAO.createMedicalReport(medicalReport)
    }

    suspend fun createImageMedicalReport(imageMedicalReport: ImageMedicalReportEntity): Long {
        return imageMedicalReportDAO.createImageMedicalReport(imageMedicalReport)
    }
}