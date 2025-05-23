package com.example.petapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.petapp.data.local.dao.GPSDeviceDAO
import com.example.petapp.data.local.dao.ImageMedicalReportDAO
import com.example.petapp.data.local.dao.MedicalReportDAO
import com.example.petapp.data.local.dao.PetDAO
import com.example.petapp.data.local.dao.PetStatisticDAO
import com.example.petapp.data.local.dao.Pet_ReminderDAO
import com.example.petapp.data.local.dao.ReminderDao
import com.example.petapp.data.local.dao.StatisticTypeDAO
import com.example.petapp.data.local.dao.UserDAO
import com.example.petapp.data.model.GPSEntity
import com.example.petapp.data.model.ImageMedicalReportEntity
import com.example.petapp.data.model.MedicalReportEntity
import com.example.petapp.data.model.PetEntity
import com.example.petapp.data.model.PetStatisticEntity
import com.example.petapp.data.model.Pet_ReminderEntity
import com.example.petapp.data.model.ReminderEntity
import com.example.petapp.data.model.StatisticTypeEntity
import com.example.petapp.data.model.UserEntity
import com.example.petapp.data.repository.MedicalReportRepository
import com.example.petapp.data.repository.PetRepository
import com.example.petapp.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

@Database(
    entities = [UserEntity::class, PetEntity::class, StatisticTypeEntity::class, PetStatisticEntity::class, ReminderEntity::class, Pet_ReminderEntity::class, GPSEntity::class, MedicalReportEntity::class, ImageMedicalReportEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun statisticTypeDAO(): StatisticTypeDAO
    abstract fun petStatisticDAO(): PetStatisticDAO
    abstract fun reminderDAO(): ReminderDao
    abstract fun petReminderDAO(): Pet_ReminderDAO
    abstract fun petDao(): PetDAO
    abstract fun gpsDeviceDAO(): GPSDeviceDAO
    abstract fun medicalReportDAO(): MedicalReportDAO
    abstract fun imageMedicalReportDAO(): ImageMedicalReportDAO


    // Singleton như thường lệ
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_app_database.db"
                )
                    .addCallback(DatabaseCallback(context)) // 🔥 Gắn callback
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(private val context: Context) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            // Chèn tài khoản admin ở đây
            CoroutineScope(Dispatchers.IO).launch {
                val userDao = getInstance(context).userDao()
                val userRepository = UserRepository(userDao)
                val petDAO = getInstance(context).petDao()
                val petRepository = PetRepository(petDAO)
                val medicalReportDAO = getInstance(context).medicalReportDAO()
                val imageMedicalReportDAO = getInstance(context).imageMedicalReportDAO()
                val medicalReportRepository =
                    MedicalReportRepository(medicalReportDAO, imageMedicalReportDAO)


                val admin = UserEntity(
                    id = "admin_Dong1412_okok",
                    username = "admin",
                    password = "admin", // nên hash mật khẩu nếu bảo mật
                    fullname = "Administrator",
                    role = "admin"
                )
                userRepository.register(admin)
                println("Admin account created: $admin")

                val petDefault = PetEntity(
                    id = "admin_Default",
                    name = "Default Pet",
                    breedName = "Default Breed",
                    birthDate = "2023-01-01",
                    gender = "Male",
                    color = "Black",
                    height = 10.0F,
                    weight = 5.0F,
                    imageUrl = "",
                    userId = "admin_Dong1412_okok"
                )
                val response = petRepository.addPet(petDefault)
                if (response == -1L) {
                    println("Failed to create default pet")
                } else {
                    println("Default pet created: $petDefault")

                    for (i in 1..5) {
                        val year = Random.nextInt(2020, 2024)
                        val medicalReportDefault = MedicalReportEntity(
                            id = "medical_report_default_$i",
                            title = "Report $i",
                            hospital = "Default Hospital",
                            veterinarian = "Default Vet",
                            description = "Normal",
                            createdAt = "$year-10-26T10:00:00Z",
                            petId = "admin_Default"
                        )
                        try {
                            medicalReportRepository.insertMedicalReport(medicalReportDefault)
                        } catch (e: Exception) {
                            println("Failed to create default medical report: ${e.message}")
                        }
                    }
                }

            }
        }
    }
}