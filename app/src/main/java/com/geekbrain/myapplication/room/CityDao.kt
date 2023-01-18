package com.geekbrain.myapplication.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CityDao {

    @Query("SELECT * FROM CityEntity")
    fun all(): List<CityEntity>

    @Query("SELECT * FROM CityEntity WHERE city LIKE :city")
    fun getDataByName (city: String): List<CityEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(cityEntity: CityEntity)

    @Update
    fun update(cityEntity: CityEntity)

    @Delete
    fun delete(cityEntity: CityEntity)
}