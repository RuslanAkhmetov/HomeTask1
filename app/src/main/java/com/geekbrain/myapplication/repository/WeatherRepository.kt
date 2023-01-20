package com.geekbrain.myapplication.repository

import androidx.annotation.RequiresApi
import com.geekbrain.myapplication.model.Weather

interface WeatherRepository {
    fun getWeatherListFromServer(listWeather:List<Weather>)
    fun getWeatherFromRepository(): MutableList<Weather>
    fun getWeatherFromLocalStorage()
    @RequiresApi(value = 24)
    fun refreshWeatherList()
}