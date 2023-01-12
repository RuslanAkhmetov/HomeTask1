package com.geekbrain.myapplication.repository

import androidx.annotation.RequiresApi
import com.geekbrain.myapplication.model.Weather

interface Repository {
    fun getWeatherFromServer(listWeather:List<Weather>)
    fun  getWeatherFromRepository(): MutableList<Weather>
    fun getWeatherFromLocalStorageRus(): List<Weather>
    fun getWeatherFromLocalStorageWorld(): List<Weather>
    @RequiresApi(value = 24)
    fun refreshWeatherList()
}