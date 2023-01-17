package com.geekbrain.myapplication.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryWeather(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val city: String,
    val isRus: Boolean,
    val lat: Float,
    val lon: Float,

)
