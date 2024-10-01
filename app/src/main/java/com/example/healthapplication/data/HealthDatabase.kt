package com.example.healthapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserHealthData::class], version = 1)
abstract class HealthDatabase : RoomDatabase() {
    abstract fun userHealthDao(): UserHealthDao
}
