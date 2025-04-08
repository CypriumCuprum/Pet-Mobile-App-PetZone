package com.example.petapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.petapp.data.local.dao.PetStatisticDAO
import com.example.petapp.data.local.dao.StatisticTypeDAO
import com.example.petapp.data.local.dao.UserDAO
import com.example.petapp.data.model.PetEntity
import com.example.petapp.data.model.PetStatisticEntity
import com.example.petapp.data.model.StatisticTypeEntity
import com.example.petapp.data.model.UserEntity

@Database(
    entities = [UserEntity::class, PetEntity::class, StatisticTypeEntity::class, PetStatisticEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun statisticTypeDAO(): StatisticTypeDAO
    abstract fun petStatisticDAO(): PetStatisticDAO
    // Singleton như thường lệ
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}