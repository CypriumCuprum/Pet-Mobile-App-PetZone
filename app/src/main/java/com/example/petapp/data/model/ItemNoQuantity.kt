package com.example.petapp.data.model;

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID
@Parcelize
data class ItemNoQuantity(
    val id: UUID,
    val name: String,
    val price: Float,
    val description: String,
    val manufacturer: String,
    val image_url: String
) : Parcelable

