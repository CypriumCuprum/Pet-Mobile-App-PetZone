package com.example.petapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded


data class MedicalReportWithPet(
    @Embedded val report: MedicalReportEntity,

    @ColumnInfo(name = "pet_name")
    val petName: String,

    @ColumnInfo(name = "pet_id")
    val petId: String
)
