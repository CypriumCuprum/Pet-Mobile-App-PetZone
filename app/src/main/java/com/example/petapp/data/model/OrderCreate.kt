package com.example.petapp.data.model

import java.util.UUID

data class OrderCreate(
    val userId: UUID,
    val listItem: List<OrderItemCreate>
)
