package com.example.healthapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_health_data")
data class UserHealthData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val respiratoryRate: Int?,
    val heartRate: Int?,
    val cough: Float,
    val fatigue: Float,
    val fever: Float,
    val headache: Float,
    val soreThroat: Float,
    val shortnessOfBreath: Float,
    val nausea: Float,
    val muscleAche: Float,
    val lossOfTasteOrSmell: Float,
    val diarrhea: Float,
    val feelingTired: Float
)
