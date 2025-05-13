package com.example.petapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import java.time.Instant

import java.util.*

@Entity(
    tableName = "pet_statistic",
    foreignKeys = [
        ForeignKey(
            entity = StatisticTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["statistic_typeid"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["id"],
            childColumns = ["petid"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PetStatisticEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "value")
    val value: Float,

    @ColumnInfo(name = "recorded_at")
    val recorded_at: String,

    @ColumnInfo(name = "note")
    val note: String?,

    @ColumnInfo(name = "created_at")
    val created_at: String = Instant.now().toString(),

    @ColumnInfo(name = "updated_at")
    val updated_at: String = Instant.now().toString(),

    @ColumnInfo(name = "petid")
    val petid: String,

    @ColumnInfo(name = "statistic_typeid")
    val statistic_typeid: UUID,

    @ColumnInfo(name = "is_synced")
    val is_synced: Boolean = false

)
