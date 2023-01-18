package com.geekbrain.myapplication.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database (entities = [CityEntity:: class], version = 1, exportSchema = false)
abstract class CityDataBase : RoomDatabase() {
    abstract fun citiDao(): CityDao
}