package com.example.petapp.data.model

import androidx.room.*
import java.util.*
import java.time.Instant

@Entity(
    tableName = "pet",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userid"])]
)
data class PetEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "breed_name")
    val breedName: String,

    @ColumnInfo(name = "gender")
    val gender: String,

    @ColumnInfo(name = "birth_date")
    val birthDate: String?, // or use `Date` with type converter

    @ColumnInfo(name = "color")
    val color: String? = null,

    @ColumnInfo(name = "height")
    val height: Float,

    @ColumnInfo(name = "weight")
    val weight: Float,

    @ColumnInfo(name = "image_url")
    val imageUrl: String,

    @ColumnInfo(name = "note")
    val note: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: String = Instant.now().toString(),  // Tự động dùng ISO 8601 UTC

    @ColumnInfo(name = "updated_at")
    val updatedAt: String = Instant.now().toString(),  // Gán tạm, sẽ cập nhật khi sửa

    @ColumnInfo(name = "userid")
    val userId: String,

    // Optional: use this to track sync status locally
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false,

    @ColumnInfo(name = "last_sync")
    val lastSync: String? = null  // use ISO 8601 format for sync
)
