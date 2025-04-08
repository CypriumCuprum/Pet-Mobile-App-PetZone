package com.example.petapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.petapp.data.local.dao.UserDAO
import com.example.petapp.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDAO

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
                val admin = UserEntity(
                    username = "admin",
                    password = "admin", // nên hash mật khẩu nếu bảo mật
                    fullname = "Administrator",
                    role = "admin"
                )
                userDao.register(admin)
            }
        }
    }
}
