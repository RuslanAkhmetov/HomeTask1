package com.geekbrain.myapplication.repository

import android.os.Handler
import android.util.Log
import com.geekbrain.myapplication.model.City
import com.geekbrain.myapplication.room.CityDao
import com.geekbrain.myapplication.room.CityEntity

private const val TAG = "LocalRepositoryImpl"

class LocalRepositoryImpl(private val localDaoSource: CityDao,
                          val  listener : DBLoadListener) : LocalRepository {

    interface DBLoadListener {
        fun onReceiveCitiesFromDB(cities: List<City>)
        fun onFailure()
    }

    override fun getAllCitiesAsync() {

        val handler = Handler()
        Thread{
            val cities = convertCityEntityToCity(localDaoSource.all())
            handler.post{
                listener.onReceiveCitiesFromDB(cities)
            }
        }.start()
    }

    override fun saveEntity(city: City) {
        //Thread {
            localDaoSource.insert(convertCityToCityEntity(city))
            Log.i(TAG, "saveEntity: ++")
        //}.start()
    }

    override fun deleteCities(finalId: Long) {
        localDaoSource.deleteLast(finalId)
    }

    override fun citiesCount(): Long {
        return localDaoSource.cityEntityCount()
    }

    private fun convertCityEntityToCity(entityList: List<CityEntity>): List<City> {
             return entityList.map {
                City(it.city, it.isRus, it.lat, it.lon)
            }
        }


    private fun convertCityToCityEntity(city: City): CityEntity {
        if (city.city != null && city.isRus != null && city.lat != null && city.lon != null) {
            return CityEntity(
                0,
                city.city!!,
                city.isRus!!,
                lat = city.lat!!,
                lon = city.lon!!
            )
        } else {
            throw java.lang.IllegalArgumentException("City.parameter  can't be null")
        }
    }

}
