package com.example.petapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date

/**
 * Entity cho bảng vaccination_schedule trong Room Database.
 * Đồng thời đóng vai trò là model cho dữ liệu lịch tiêm trong toàn bộ ứng dụng.
 */
@Entity(tableName = "vaccination_schedule")
data class VaccinationScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "schedule_id")
    val scheduleId: Long = 0,

    @ColumnInfo(name = "pet_id")
    val petId: Long,

    @ColumnInfo(name = "vaccine_type")
    val vaccineType: String,

    @ColumnInfo(name = "vaccination_date")
    val vaccinationDate: Date,

    @ColumnInfo(name = "vaccination_time")
    val vaccinationTime: String,

    @ColumnInfo(name = "clinic")
    val clinic: Long,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "reminder_before_days")
    val reminderBeforeDays: Int?,

    @ColumnInfo(name = "reminder_before_hours")
    val reminderBeforeHours: Int?,

    @ColumnInfo(name = "created_at")
    val createdAt: Date,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Date
) {
    companion object {
        // Các hằng số cho trạng thái
        const val STATUS_UPCOMING = "UPCOMING"
        const val STATUS_COMPLETED = "COMPLETED"
        const val STATUS_CANCELLED = "CANCELLED"
    }
}