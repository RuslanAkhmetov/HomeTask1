package com.geekbrain.myapplication.repository

import com.geekbrain.myapplication.model.Weather

interface Repository {
    fun getWeatherFromServer(): Weather
    fun getWeatherFromLocalStorage(): Weather
}