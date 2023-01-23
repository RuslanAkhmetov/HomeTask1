package com.geekbrain.myapplication.repository

import com.geekbrain.myapplication.model.City

interface LocalRepository {
    fun getAllCitiesAsync()
    fun saveEntity(city: City)
    fun deleteCities(id: Long)
    fun citiesCount()
}