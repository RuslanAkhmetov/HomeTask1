package com.geekbrain.myapplication.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database (entities = [CityEntity:: class, RequestEntity::class], version = 2, exportSchema = false)

abstract class CityDataBase : RoomDatabase() {
    abstract fun citiDao(): CityDao
}