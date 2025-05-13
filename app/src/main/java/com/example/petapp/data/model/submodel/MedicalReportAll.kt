package com.example.petapp.data.model.submodel

import com.example.petapp.data.model.PetEntity

data class MedicalReportExtend(
    val id: String,
    val title: String,
    val hospital: String,
    val veterinarian: String,
    val description: String,
    val createdAt: String,
    val petName: String,
    val images: List<String>,
)
