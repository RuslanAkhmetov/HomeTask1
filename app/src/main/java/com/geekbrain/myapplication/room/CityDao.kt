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
    suspend fun all(): List<CityEntity>

    @Query("SELECT * FROM CityEntity WHERE city LIKE :city")
    suspend fun getDataByName (city: String): List<CityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cityEntity: CityEntity)

    @Update
    suspend fun update(cityEntity: CityEntity)

    @Delete
    suspend fun delete(cityEntity: CityEntity)

    @Query("DELETE FROM CityEntity WHERE id > :finalId")
    suspend fun deleteLast(finalId: Long)

    @Query("SELECT COUNT(*) from CityEntity")
    suspend fun cityEntityCount() : Long
}