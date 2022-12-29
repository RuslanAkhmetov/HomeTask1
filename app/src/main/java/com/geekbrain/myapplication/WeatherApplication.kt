package com.geekbrain.myapplication

import android.app.Application
import com.geekbrain.myapplication.repository.WeatherRepository

class WeatherApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        WeatherRepository.initialize(this)
    }
}