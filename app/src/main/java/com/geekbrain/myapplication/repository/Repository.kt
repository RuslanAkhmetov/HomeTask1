package com.geekbrain.myapplication.repository

import com.geekbrain.myapplication.model.Weather

interface Repository {
    fun getWeatherFromServer(): Weather

    fun getWeatherFromLocalStorageRus(): List<Weather>
    fun getWeatherFromLocalStorageWorld(): List<Weather>
}