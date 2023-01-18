package com.geekbrain.myapplication.repository

import com.geekbrain.myapplication.model.City
import com.geekbrain.myapplication.room.CityDao
import com.geekbrain.myapplication.room.CityEntity

class LocalRepositoryImpl(private val localDaoSource: CityDao) : LocalRepository {
    override fun getAllCities(): List<City> {
        return convertCityEntityToCity(localDaoSource.all())
    }

    private fun convertCityEntityToCity(entityList: List<CityEntity>): List<City> {
        return entityList.map {
            City(it.city, it.isRus, it.lat, it.lon)
        }
    }

    override fun saveEntity(city: City) {
        localDaoSource.insert(convertCityToCityEntity(city))
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
