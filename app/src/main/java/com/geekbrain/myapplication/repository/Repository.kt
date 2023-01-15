package com.geekbrain.myapplication.repository

import androidx.annotation.RequiresApi
import com.geekbrain.myapplication.model.City
import com.geekbrain.myapplication.model.Weather
import com.geekbrain.myapplication.model.weatherDTO.WeatherDTO
import com.geekbrain.myapplication.model.geoKod.CoordinatesDTO
import retrofit2.Callback

interface Repository {
    fun getWeatherListFromServer(listWeather:List<Weather>)
    fun getWeatherFromRemoteSourse(lat: Float, lon: Float, callback: Callback<WeatherDTO>)
    fun getCityCoordinates(city: City, callback: Callback<CoordinatesDTO>)
    fun  getWeatherFromRepository(): MutableList<Weather>
    fun getWeatherFromLocalStorageRus(): List<Weather>
    fun getWeatherFromLocalStorageWorld(): List<Weather>
    @RequiresApi(value = 24)
    fun refreshWeatherList()
}