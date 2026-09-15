package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.TripBookingEntity

@Database(
    entities = [TripBookingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TomnayaDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao

    companion object {
        @Volatile
        private var INSTANCE: TomnayaDatabase? = null

        fun getInstance(context: Context): TomnayaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TomnayaDatabase::class.java,
                    "tomnaya_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
