package com.example.petapp.data.model

import java.util.UUID

data class OrderItemEntity (
    val id: UUID,
    val cartItem: CartItemEntity
)