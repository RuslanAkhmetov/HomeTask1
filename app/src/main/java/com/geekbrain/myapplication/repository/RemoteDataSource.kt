package com.geekbrain.myapplication.repository

import com.geekbrain.myapplication.BuildConfig
import com.geekbrain.myapplication.model.City
import com.geekbrain.myapplication.model.weatherDTO.WeatherDTO
import com.geekbrain.myapplication.model.geoKod.CoordinatesDTO
import com.google.gson.GsonBuilder
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {
    private val weatherAPI = Retrofit.Builder()
        .baseUrl("https://api.weather.yandex.ru/")
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            )
        )
        .build().create(WeatherAPI::class.java)

    fun getWeather(lat: Float, lon: Float, callback: Callback<WeatherDTO>) {
        weatherAPI.loaderWeather(BuildConfig.WEATHER_API_KEY, lat, lon)
            .enqueue(callback)
    }

    private val getGeoKodAPI = Retrofit.Builder()
        .baseUrl("https://geocode-maps.yandex.ru/")
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            )
        )
        .build().create(GetGeoKodAPI::class.java)

    fun getCoordinates(city: City, callback: Callback<CoordinatesDTO>) {
        city.city?.let {
            getGeoKodAPI.getCoordinates(BuildConfig.GEOKOD_API_KEY, it, format = "json")
                .enqueue(callback)
        }
    }

    fun getCityName(lat: Float?, lon: Float?, callback: Callback<CoordinatesDTO>){
        if (lat != null && lon != null){
            val latAndLon = "$lon,$lat"
            getGeoKodAPI.getCityName(BuildConfig.GEOKOD_API_KEY, latAndLon, format = "json")
                .enqueue(callback)
        }
    }


}