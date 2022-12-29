package com.geekbrain.myapplication.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.geekbrain.myapplication.model.*
import com.geekbrain.myapplication.viewmodel.CoordinatesLoader
import com.geekbrain.myapplication.viewmodel.WeatherLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository : Repository {

    private val TAG = "WeatherRepository"

    private var listWeatherReceived: MutableList<Weather> = mutableListOf()

    override fun getWeatherFromRepository(): MutableList<Weather> = listWeatherReceived


    @RequiresApi(Build.VERSION_CODES.N)
    override suspend fun refreshWeatherList() {

        withContext(Dispatchers.IO) {
            try {
                getWeatherFromServer(getWeatherFromLocalStorageRus() + getWeatherFromLocalStorageWorld())
            } catch (e: Exception){
                throw e
            }

        }
    }

    private val coordinatesLoaderListener =
        object : CoordinatesLoader.CoordinateLoaderListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onLoaded(city: City) {
                WeatherLoader(onLoaderListener, city).apply {
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
            override fun onLoaded(weather: Weather) {
                listWeatherReceived.add(weather)
            }

            override fun onFailed(throwable: Throwable) {
                Log.i(TAG, "weatherLoaderFailed: " + throwable.message)
                throw throwable
            }

        }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getWeatherFromServer(listWeather: List<Weather>) {

        for (weatherItem in listWeather) {
            try{
            if (weatherItem.city.lat == null || weatherItem.city.lon == null) {
                CoordinatesLoader(coordinatesLoaderListener, weatherItem.city)
                    .also {
                        it.getCoordinates()
                    }
            } else {
                val loader = WeatherLoader(onLoaderListener, weatherItem.city)
                loader.loaderWeather()
            }
            } catch (e: Exception){
                Log.i(TAG, "getWeatherFromServerFailed: " + e.message)
                throw e
            }
        }
        Log.i(TAG, "getWeatherFromServer: listWeatherReceived " + listWeatherReceived.size)
    }

    override fun getWeatherFromLocalStorageRus(): List<Weather> = getRussianCities()

    override fun getWeatherFromLocalStorageWorld(): List<Weather> = getWorldCities()


}