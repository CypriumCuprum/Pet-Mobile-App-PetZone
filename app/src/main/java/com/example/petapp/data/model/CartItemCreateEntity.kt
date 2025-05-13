package com.example.petapp.data.model

import java.util.UUID

data class CartItemCreateEntity(
    val quantity: Int,
    val itemId: UUID,
    val userId: UUID
)
