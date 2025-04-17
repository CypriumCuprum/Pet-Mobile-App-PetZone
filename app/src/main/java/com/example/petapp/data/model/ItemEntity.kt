package com.example.petapp.data.model

import java.util.UUID

data class ItemEntity(
    val id: UUID,
    val name: String,
    val description: String,
    val quantity: Int,
    val manufacturer: String
)
