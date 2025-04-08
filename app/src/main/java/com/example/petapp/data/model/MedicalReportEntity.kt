package com.example.petapp.data.model

import androidx.room.*
import java.util.*
import java.time.Instant

@Entity(
    tableName = "medical_report",
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
data class MedicalReportEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "hospital")
    val hospital: String,

    @ColumnInfo(name = "veterinarian")
    val veterinarian: String,

    @ColumnInfo(name = "description")
    val description: String,

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
