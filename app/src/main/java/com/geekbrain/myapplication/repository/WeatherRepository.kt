package com.geekbrain.myapplication.repository

import com.geekbrain.myapplication.model.Weather

class WeatherRepository: Repository {
    override fun getWeatherFromServer(): Weather {
        return Weather()
    }

    override fun getWeatherFromLocalStorage(): Weather {
        return Weather()
    }
}