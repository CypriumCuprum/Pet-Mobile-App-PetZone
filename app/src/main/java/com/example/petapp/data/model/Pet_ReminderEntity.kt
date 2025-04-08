package com.example.petapp.data.model
import androidx.room.*
import java.util.*

@Entity(
    tableName ="pet_reminder",
    foreignKeys = [
        ForeignKey(
            entity = Pet::class,
            parentColumns = ["id"],
            childColumns = ["petid"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ReminderEntity::class,
            parentColumns = ["id"],
            childColumns = ["reminderid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class Pet_ReminderEntity (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(), // Khởi tạo giá trị UUID mặc định khi tạo đối tượng mới

    @ColumnInfo(name = "petid")
    val petId: String, // ID của thú cưng (khóa ngoại từ bảng Pet)

    @ColumnInfo(name = "reminderid")
    val reminderId: String, // ID của nhắc nhở (khóa ngoại từ bảng ReminderEntity)
)