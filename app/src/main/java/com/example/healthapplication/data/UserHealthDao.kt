package com.example.healthapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserHealthDao {
    @Insert
    suspend fun insertHealthData(userHealthData: UserHealthData)

    @Query("SELECT * FROM user_health_data WHERE id = :id")
    suspend fun getHealthData(id: Int): UserHealthData?
}
