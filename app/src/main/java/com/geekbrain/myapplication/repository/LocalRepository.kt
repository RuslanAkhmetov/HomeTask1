package com.geekbrain.myapplication.repository

import com.geekbrain.myapplication.model.City
import com.geekbrain.myapplication.model.RequestLog

interface LocalRepository {
    fun getAllCitiesAsync()
    fun saveEntity(city: City)
    fun deleteCities(id: Long)
    fun citiesCount()
    fun getRequestLog()
    fun saveRequestToLog(requestLog: RequestLog)
}