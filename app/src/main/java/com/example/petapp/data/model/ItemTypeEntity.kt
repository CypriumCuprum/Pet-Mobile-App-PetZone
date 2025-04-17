package com.example.petapp.data.model
import com.example.petapp.R
import java.util.Locale
import java.util.UUID

data class ItemTypeEntity (
    val id: UUID,
    val name: String,
    val unit: String,
    val note: String,
    val listItem: List<ItemEntity>
    )

fun ItemTypeEntity.getIconResId(): Int {
    return when (name.lowercase(Locale.getDefault())) {
        "food" -> R.drawable.foodshop
        "vet" -> R.drawable.vetitemshop
        "accessories" -> R.drawable.accessories1
        "iot" -> R.drawable.foodshop
        else -> R.drawable.bg_main_color_15dp_corner
    }
}
