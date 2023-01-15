package com.geekbrain.myapplication.repository

import com.geekbrain.myapplication.model.weatherDTO.WeatherDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface WeatherAPI {
    @GET("v2/forecast")
    fun loaderWeather(
        @Header("X-Yandex-API-Key") token : String,
        @Query("lat") lat: Float,
        @Query("lon") lon: Float,
    ) : Call<WeatherDTO>
}