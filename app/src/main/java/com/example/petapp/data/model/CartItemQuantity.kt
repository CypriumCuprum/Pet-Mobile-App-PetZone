package com.example.petapp.data.model

import java.util.UUID

data class CartItemQuantity (
    val cartItemId: UUID,
    val quantity: Int
)