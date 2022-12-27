package com.geekbrain.myapplication.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.geekbrain.myapplication.model.Weather
import com.geekbrain.myapplication.model.WeatherDTO
import com.geekbrain.myapplication.model.getRussianCities
import com.geekbrain.myapplication.model.getWorldCities
import com.geekbrain.myapplication.viewmodel.CoordinatesLoader
import com.geekbrain.myapplication.viewmodel.WeatherLoader

class WeatherRepository : Repository {

    private val TAG = "WeatherRepository"

    private var weather: Weather = Weather(weatherDTO = null)

    private var listWeatherReceived: MutableList<Weather> = mutableListOf()

    private val coordinatesLoaderListener =
        object : CoordinatesLoader.CoordinateLoaderListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onLoaded(pos: String) {

                val fl = pos.split(" ").map { it.toFloat() }
                weather.city.lon = fl[0]
                weather.city.lat = fl[1]
                Log.i(TAG, "onLoaded: $weather.city.lat $weather.city.lon")
                WeatherLoader(onLoaderListener, weather.city.lat, weather.city.lon).apply {
                    loaderWeather()
                }
            }

            override fun onFailed(throwable: Throwable) {
                Log.i(TAG, "CoordinatesLoaderFailed: " + throwable.message)
                throw throwable
            }

        }

    private val onLoaderListener: WeatherLoader.WeatherLoaderListener =
        object : WeatherLoader.WeatherLoaderListener {
            override fun onLoaded(weatherDTO: WeatherDTO) {
                weather.weatherDTO = weatherDTO
                Log.i(TAG, "onLoaded: ${weather.toString()}")
                listWeatherReceived.add(weather)
                Log.i(TAG, "onLoaded: "+ listWeatherReceived.size)
            }

            override fun onFailed(throwable: Throwable) {
                Log.i(TAG, "weatherLoaderFailed: " + throwable.message)
                throw throwable
            }

        }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getWeatherFromServer(listWeather: List<Weather>): List<Weather> {
        for (weather in listWeather) {
            Log.i(TAG, "getWeatherFromServer: $weather")
            if (weather.city.lat == null || weather.city.lon == null) {
                    weather.city.city?.let { it ->
                        CoordinatesLoader(coordinatesLoaderListener, it)
                            .also {
                                it.getCoordinates()
                            }
                    }
            } else {
                val loader = WeatherLoader(onLoaderListener, weather.city.lat, weather.city.lon)
                loader.loaderWeather()
            }
        }
        Log.i(TAG, "getWeatherFromServer: listWeatherReceived" + listWeatherReceived.size)
        return listWeatherReceived
    }

    override fun getWeatherFromLocalStorageRus(): List<Weather> = getRussianCities()

    override fun getWeatherFromLocalStorageWorld(): List<Weather> = getWorldCities()

}