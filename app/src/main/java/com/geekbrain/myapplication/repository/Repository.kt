package com.geekbrain.myapplication.repository

import com.geekbrain.myapplication.model.Weather

interface Repository {
    fun getWeatherFromServer(listWeather:List<Weather>): List<Weather>

    fun getWeatherFromLocalStorageRus(): List<Weather>
    fun getWeatherFromLocalStorageWorld(): List<Weather>
}