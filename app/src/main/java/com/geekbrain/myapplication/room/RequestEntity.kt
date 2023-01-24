package com.geekbrain.myapplication.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RequestEntity(
    @PrimaryKey
    val timestamp: Long = 0,
    val cityId: Long,
    val temperature: Float,
    val condition: String
)
