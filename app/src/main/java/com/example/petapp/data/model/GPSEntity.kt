package com.example.petapp.data.model

import androidx.room.*
import java.util.*
import java.time.Instant

@Entity(
    tableName = "gps_device",
    foreignKeys = [
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["id"],
            childColumns = ["petid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["petid"])]
)
data class GPSEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "latitude")
    val latitude: Double = -1.0,

    @ColumnInfo(name = "longitude")
    val longitude: Double = -1.0,

    @ColumnInfo(name = "status")
    val status: String = "Offline",  // default status

    @ColumnInfo(name = "battery")
    val battery: Int = -1,  // default battery level

    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: String = Instant.now().toString(),  // Tự động dùng ISO 8601 UTC

    @ColumnInfo(name = "updated_at")
    val updatedAt: String = Instant.now().toString(),  // Gán tạm, sẽ cập nhật khi sửa

    @ColumnInfo(name = "petid")
    val petId: String,

    // Optional: use this to track sync status locally
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false,

    @ColumnInfo(name = "last_sync")
    val lastSync: String? = null  // use ISO 8601 format for sync
)
