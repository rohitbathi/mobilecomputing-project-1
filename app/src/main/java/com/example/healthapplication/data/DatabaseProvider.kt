package com.example.healthapplication.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var INSTANCE: HealthDatabase? = null

    fun getDatabase(context: Context): HealthDatabase {
        if (INSTANCE == null) {
            synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    HealthDatabase::class.java,
                    "health_db"
                ).build()
            }
        }
        return INSTANCE!!
    }
}
