package com.example.petapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.*

@Entity(tableName = "statistic_type")
data class StatisticTypeEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "unit")
    val unit: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "created_at")
    val created_at: String = Instant.now().toString(),

    @ColumnInfo(name = "updated_at")
    val updated_at: String = Instant.now().toString(),

    @ColumnInfo(name = "is_synced")
    val is_synced: Boolean = false
)
