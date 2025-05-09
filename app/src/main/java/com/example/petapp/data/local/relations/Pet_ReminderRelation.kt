package com.example.petapp.data.local.relations
import androidx.room.*
import com.example.petapp.data.model.PetEntity
import com.example.petapp.data.model.Pet_ReminderEntity
import com.example.petapp.data.model.ReminderEntity

data class PetWithReminders(
    @Embedded val pet: PetEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = Pet_ReminderEntity::class,
            parentColumn = "pet_id",
            entityColumn = "reminder_id"
        )

    )
    val reminders: List<ReminderEntity>
)

data class ReminderWithPets(
    @Embedded val reminder: ReminderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = Pet_ReminderEntity::class,
            parentColumn = "reminder_id",
            entityColumn = "pet_id"
        )
    )
    val pets: List<PetEntity>
)