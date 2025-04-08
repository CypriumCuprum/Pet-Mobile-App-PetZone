package com.example.petapp.data.model
import androidx.room.*
import java.util.*;

@Entity(
    tableName = "reminder",
)
data class ReminderEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") // Thêm ColumnInfo cho rõ ràng (tùy chọn)
    val id: String = UUID.randomUUID().toString(), // Khởi tạo giá trị UUID mặc định khi tạo đối tượng mới

    @ColumnInfo(name = "type")
    val type: Int,

    @ColumnInfo(name = "time_reminder")
    val timeReminder: Long, // Vẫn dùng Long cho timestamp

    @ColumnInfo(name = "repeat")
    val repeat: String,

    @ColumnInfo(name = "create_at")
    val createdAt: Long, // Vẫn dùng Long cho date

    @ColumnInfo(name = "update_at")
    val updatedAt: Long, // Vẫn dùng Long cho date

    @ColumnInfo(name = "note")
    val note: String?,

    @ColumnInfo(name = "status")
    val status: String
)