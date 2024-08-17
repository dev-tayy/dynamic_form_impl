package com.example.myapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.data.model.BuildingMapping

@Database(entities = [BuildingMapping::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun buildingMappingDao(): BuildingMappingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Provides a singleton instance of the database.
         *
         * @param context The application context.
         * @return An instance of the AppDatabase.
         */
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
