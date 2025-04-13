package com.example.petapp.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.petapp.data.model.PetEntity
import com.example.petapp.data.model.VaccinationScheduleEntity

/**
 * Lớp quan hệ (Relation) biểu diễn mối quan hệ 1-n giữa Pet và VaccinationSchedule.
 * Một thú cưng có thể có nhiều lịch tiêm chủng.
 *
 * @property pet Thông tin thú cưng (bảng chính)
 * @property vaccinationSchedules Danh sách lịch tiêm chủng của thú cưng (bảng phụ)
 */
data class PetWithVaccinationSchedules(
    @Embedded val pet: PetEntity,

    @Relation(
        parentColumn = "id",  // Khóa chính của bảng Pet
        entityColumn = "pet_id"  // Khóa ngoại trong bảng VaccinationSchedule
    )
    val vaccinationSchedules: List<VaccinationScheduleEntity>
)