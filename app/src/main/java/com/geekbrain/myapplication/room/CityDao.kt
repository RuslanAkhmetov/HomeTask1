package com.geekbrain.myapplication.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.geekbrain.myapplication.model.RequestLog

@Dao
interface CityDao {

    @Query("SELECT * FROM CityEntity")
    fun all(): List<CityEntity>

    @Query("SELECT city, timeStamp, temperature, condition FROM RequestEntity JOIN CityEntity ON RequestEntity.cityId = CityEntity.id")
    fun allRequest() : List<RequestLog>

    @Query("SELECT * FROM CityEntity WHERE city LIKE :city")
    fun getDataByName (city: String): List<CityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cityEntity: CityEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRequest(request: RequestEntity)

    @Update
    fun update(cityEntity: CityEntity)

    @Delete
    fun delete(cityEntity: CityEntity)

    @Query("DELETE FROM CityEntity WHERE id > :finalId")
    fun deleteLast(finalId: Long)

    @Query("SELECT COUNT(*) from CityEntity")
    fun cityEntityCount() : Long

    @Query("SELECT id FROM CityEntity WHERE city = :cityName")
    fun getCityId(cityName: String): Long
}