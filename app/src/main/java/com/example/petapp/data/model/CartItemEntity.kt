package com.example.petapp.data.model

import java.util.UUID

data class CartItemEntity(
    val id: UUID,
    val quantity: Int,
    val item: ItemNoQuantity
)
