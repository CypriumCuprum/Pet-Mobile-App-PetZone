package com.example.petapp.data.model

import androidx.room.*
import java.util.*
import java.time.Instant

@Entity(
    tableName = "image_medical_report",
    foreignKeys = [
        ForeignKey(
            entity = MedicalReportEntity::class,
            parentColumns = ["id"],
            childColumns = ["medical_reportid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["medical_reportid"])]
)
data class ImageMedicalReportEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "image_url")
    val imageUrl: String,

    @ColumnInfo(name = "created_at")
    val createdAt: String = Instant.now().toString(),  // Tự động dùng ISO 8601 UTC

    @ColumnInfo(name = "updated_at")
    val updatedAt: String = Instant.now().toString(),  // Gán tạm, sẽ cập nhật khi sửa

    @ColumnInfo(name = "medical_reportid")
    val medicalReportId: String,

    // Optional: use this to track sync status locally
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false,

    @ColumnInfo(name = "last_sync")
    val lastSync: String? = null  // use ISO 8601 format for sync
)
