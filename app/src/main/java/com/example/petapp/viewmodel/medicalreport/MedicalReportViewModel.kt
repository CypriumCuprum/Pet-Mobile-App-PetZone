package com.example.petapp.viewmodel.medicalreport

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.petapp.data.local.AppDatabase
import com.example.petapp.data.model.ImageMedicalReportEntity
import com.example.petapp.data.model.MedicalReportEntity
import com.example.petapp.data.model.submodel.MedicalReportExtend
import com.example.petapp.data.repository.MedicalReportRepository
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class MedicalReportViewModel(application: Application) : AndroidViewModel(application) {
    // Add your repository and other properties here
    private val repository: MedicalReportRepository

    init {
        // Initialize your repository here
        val medicalReportDao = AppDatabase.getInstance(application).medicalReportDAO()
        val imageMedicalReportDAO = AppDatabase.getInstance(application).imageMedicalReportDAO()
        repository = MedicalReportRepository(medicalReportDao, imageMedicalReportDAO)
    }

    suspend fun saveMedicalReport(
        id: String? = null,
        title: String,
        hospital: String,
        veterinarian: String,
        description: String,
        petId: String,
        imageUrlList: List<Pair<String?, String>>
    ) {
        val medicalReportId = id ?: UUID.randomUUID().toString()
        val medicalReport = MedicalReportEntity(
            id = medicalReportId,
            title = title,
            hospital = hospital,
            veterinarian = veterinarian,
            description = description,
            petId = petId
        )
        try {
            repository.insertMedicalReport(medicalReport)
            imageUrlList.map { (id, imageUrl) ->
                val check: String? = id
                val imageMedicalReport = ImageMedicalReportEntity(
                    id = id ?: UUID.randomUUID().toString(),
                    imageUrl = imageUrl,
                    medicalReportId = medicalReportId
                )
                if (check.isNullOrBlank()) {
                    repository.createImageMedicalReport(imageMedicalReport)
                }
            }
        } catch (e: Exception) {
            // Handle the exception
            e.printStackTrace()
        }
    }

    suspend fun getAllMedicalReportByPetIdList(
        petIdList: List<String>,
        petName: Map<String, String>
    ): List<MedicalReportExtend> {
        val medicalReportList = mutableListOf<MedicalReportExtend>()
        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        petIdList.forEach { petId ->
            val medicalReport = repository.getAllMedicalReportsByPetId(petId)
            println("medicalReport in medical report view model: $medicalReport")
            medicalReport.forEach { report ->
                val imageMedicalReport =
                    repository.getAllImageMedicalReportByMedicalReportId(report.id)
                val imageUrlList = imageMedicalReport.map { it.imageUrl }
                println("imageMedicalReport in medical report view model: $imageMedicalReport")


                val parsedDateTime: OffsetDateTime = OffsetDateTime.parse(report.createdAt)
                val formattedDate = parsedDateTime.format(outputFormatter)
                val medicalReportExtend = MedicalReportExtend(
                    id = report.id,
                    title = report.title,
                    hospital = report.hospital,
                    veterinarian = report.veterinarian,
                    description = report.description,
                    createdAt = formattedDate,
                    petName = petName[petId].toString(),
                    images = imageUrlList
                )
                medicalReportList.add(medicalReportExtend)
                println("medicalReportExtend in medical report view model: $medicalReportExtend")
            }
        }
        println("medicalReportList in medical report view model: $medicalReportList")
        return medicalReportList
    }


    // Add your methods to interact with the repository here
    @Suppress("UNCHECKED_CAST")
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MedicalReportViewModel::class.java)) {
                return MedicalReportViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}