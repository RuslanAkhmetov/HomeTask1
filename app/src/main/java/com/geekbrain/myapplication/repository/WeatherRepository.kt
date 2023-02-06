package com.geekbrain.myapplication.repository

import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.geekbrain.myapplication.model.City
import com.geekbrain.myapplication.model.RequestLog
import com.geekbrain.myapplication.model.Weather

interface WeatherRepository {
    fun getWeatherFromRepository(): MutableList<Weather>
    fun getWeatherFromLocalStorage()
    //fun getCity(cityName: String)
    var listWeatherLiveDataFromRepo :MutableLiveData<MutableList<Weather>>

    @RequiresApi(value = 24)
    fun refreshWeatherList()
    fun saveRequestToDB(requestLog: RequestLog)
    var requestLogLiveData:MutableLiveData<MutableList<RequestLog>>
    fun makeRequestLog()
    fun getRequestsLog(): MutableList<RequestLog>
    fun saveCityToDB(city: City)

}