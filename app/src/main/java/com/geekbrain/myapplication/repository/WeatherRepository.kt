package com.geekbrain.myapplication.repository

import com.geekbrain.myapplication.model.Weather
import com.geekbrain.myapplication.model.getRussianCities
import com.geekbrain.myapplication.model.getWorldCities

class WeatherRepository: Repository {
    override fun getWeatherFromServer(): Weather = Weather()

    override fun getWeatherFromLocalStorageRus(): List<Weather> =  getRussianCities()

    override fun getWeatherFromLocalStorageWorld(): List<Weather> = getWorldCities()

}