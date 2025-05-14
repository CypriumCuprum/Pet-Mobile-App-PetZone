package com.example.petapp.data.repository

import com.example.petapp.data.local.dao.ImageMedicalReportDAO
import com.example.petapp.data.local.dao.MedicalReportDAO
import com.example.petapp.data.model.ImageMedicalReportEntity
import com.example.petapp.data.model.MedicalReportEntity
import com.example.petapp.data.model.MedicalReportWithPet

class MedicalReportRepository(
    private val medicalReportDAO: MedicalReportDAO,
    private val imageMedicalReportDAO: ImageMedicalReportDAO
) {

    suspend fun getMedicalReportById(medicalReportId: String?): MedicalReportEntity {
        return medicalReportDAO.getMedicalReportById(medicalReportId)
    }

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

    suspend fun getAllMedicalReportByUserId(userId: String): List<MedicalReportWithPet> {
        return medicalReportDAO.getAllMedicalReportByUserId(userId)
    }

    suspend fun deleteImageMedicalReport(imageMedicalReportId: String): Int {
        return imageMedicalReportDAO.deleteImageMedicalReport(imageMedicalReportId)
    }

    suspend fun deleteMedicalReport(medicalReportId: String): Int {
        return medicalReportDAO.deleteMedicalReport(medicalReportId)
    }

    suspend fun getAllMedicalReportByUserIdWithFilterAndSort(
        userId: String,
        numItem: Int,
        page: Int,
        startDate: String,
        endDate: String
    ): List<MedicalReportWithPet> {
        return medicalReportDAO.getAllMedicalReportByUserIdWithFilterAndSort(
            userId,
            numItem,
            page,
            startDate,
            endDate
        )
    }

}