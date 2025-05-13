package com.example.petapp.data.model

import java.util.UUID

data class CartEntity (
    val id: UUID,
    val userId: UUID,
    val listItem: List<CartItemEntity>
)