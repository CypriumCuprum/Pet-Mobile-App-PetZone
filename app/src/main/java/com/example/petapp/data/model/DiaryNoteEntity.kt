package com.example.petapp.data.model
import androidx.room.*
import java.util.*

@Entity(
    tableName = "diary_note",
    foreignKeys = [
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["id"],
            childColumns = ["petid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
class DiaryNoteEntity (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(), // Khởi tạo giá trị UUID mặc định khi tạo đối tượng mới

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "start_time")
    val startTime: Long,

    @ColumnInfo(name = "end_time")
    val endTime: Long,

    @ColumnInfo(name = "note")
    val note: String?,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    @ColumnInfo(name = "petid")
    val petId: String
)