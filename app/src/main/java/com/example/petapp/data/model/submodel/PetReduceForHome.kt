package com.example.petapp.data.model.submodel

import androidx.room.ColumnInfo

data class PetReduceForHome(
    val id: String,
    val name: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String?
)
