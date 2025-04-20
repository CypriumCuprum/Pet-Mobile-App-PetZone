package com.example.petapp.data.model
import androidx.room.*
import com.example.petapp.R
import java.time.Instant
import java.util.*;

@Entity(
    tableName = "reminder",
)
data class ReminderEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") // Thêm ColumnInfo cho rõ ràng (tùy chọn)
    val id: String = UUID.randomUUID().toString(), // Khởi tạo giá trị UUID mặc định khi tạo đối tượng mới

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "time_reminder")
    val timeReminder: String, // Vẫn dùng Long cho timestamp

    @ColumnInfo(name = "repeat")
    val repeat: String,

    @ColumnInfo(name = "create_at")
    val createdAt: String = Instant.now().toString(), // Vẫn dùng Long cho date

    @ColumnInfo(name = "update_at")
    val updatedAt: String = Instant.now().toString(), // Vẫn dùng Long cho date

    @ColumnInfo(name = "note")
    val note: String?,

    @ColumnInfo(name = "status")
    val status: Boolean
){
    fun title():String {
        return when (type) {
            "feed"-> "Feeding"
            "vet" -> "Vet Visit"
            "medicine" -> "Medicine"
            "groom" -> "Grooming"
            "walk" -> "Walking"
            else -> "Other Reminder"
        }
    }
    fun icon():Int {
        return when (type) {
            "feed"-> R.drawable.pet_food_reminder
            "vet" -> R.drawable.veterinary_reminder
            "medicine" -> R.drawable.medicine_reminder
            "groom" -> R.drawable.grooming_reminder
            "walk" -> R.drawable.walking_reminder
            else -> R.drawable.pet_food_reminder
        }
    }
    fun time1():String {
        val timeParts = timeReminder.split(" ")
        val time = timeParts[1]
        return time
    }
    fun date1():String {
        val timeParts = timeReminder.split(" ")
        val date = timeParts[0]
        return date
    }
}