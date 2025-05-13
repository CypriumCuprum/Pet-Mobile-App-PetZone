package com.example.petapp.data.model

import java.util.UUID

class OrderEntity (
    val id: UUID,
    val listOrderItem: List<OrderItemEntity>,
    val user: UserForOrder
)