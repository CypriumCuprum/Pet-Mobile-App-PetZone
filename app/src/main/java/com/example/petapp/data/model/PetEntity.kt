package com.example.petapp.data.model

import androidx.room.*
import java.util.*

@Entity(
    tableName = "pet",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userid"])]
)
data class Pet(
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
    val imageUrl: String? = null,

    @ColumnInfo(name = "note")
    val note: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: String,  // use ISO 8601 format for sync

    @ColumnInfo(name = "updated_at")
    val updatedAt: String,

    @ColumnInfo(name = "userid")
    val userId: String,

    // Optional: use this to track sync status locally
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false,

    @ColumnInfo(name = "last_sync")
    val lastSync: String? = null  // use ISO 8601 format for sync
)
