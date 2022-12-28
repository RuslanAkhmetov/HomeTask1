package com.geekbrain.myapplication.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.geekbrain.myapplication.model.*
import com.geekbrain.myapplication.viewmodel.CoordinatesLoader
import com.geekbrain.myapplication.viewmodel.WeatherLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Thread.sleep
import java.util.*

class WeatherRepository : Repository {

    private val TAG = "WeatherRepository"

    private lateinit var weather: Weather //= Weather(weatherDTO = null)

    private var listWeatherReceived:  MutableList<Weather> = mutableListOf()

    override fun getWeatherFromRepository(): MutableList<Weather> = listWeatherReceived

    @RequiresApi(Build.VERSION_CODES.N)
    override suspend fun refreshWeatherList(){
        withContext(Dispatchers.IO){
            getWeatherFromServer(getWeatherFromLocalStorageRus())
        }
    }

    private val coordinatesLoaderListener =
        object : CoordinatesLoader.CoordinateLoaderListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onLoaded(city: City) {


                Log.i(TAG, "onLoadedCoordinates: ${city.city} lat = ${city.lat} lon = ${city.lon}")
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
                Log.i(TAG, "onLoadedWeather: ${weather.toString()}")
                listWeatherReceived.add(weather)
                Log.i(TAG, "onLoaded: "+ listWeatherReceived.size)
            }

            override fun onFailed(throwable: Throwable) {
                Log.i(TAG, "weatherLoaderFailed: " + throwable.message)
                throw throwable
            }

        }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getWeatherFromServer(listWeather: List<Weather>){//: List<Weather> {
        for (weatherItem in listWeather) {
            //weather = weatherItem
            if (weatherItem.city.lat == null || weatherItem.city.lon == null) {
                    weatherItem.city?.let { it ->
                        Log.i(TAG, "getWeatherFromServer before get Coordinates: $it")

                        CoordinatesLoader(coordinatesLoaderListener, it)
                            .also {
                                //Log.i(TAG, "getWeatherFromServer before get Coordinates: ${weather.city.city}")
                                it.getCoordinates()
                            }
                    }
            } else {
                val loader = WeatherLoader(onLoaderListener, weatherItem.city)
                loader.loaderWeather()
            }
        }
        sleep(5000)
        Log.i(TAG, "getWeatherFromServer: listWeatherReceived" + listWeatherReceived.size)
        //return listWeatherReceived
    }

    override fun getWeatherFromLocalStorageRus(): List<Weather> = getRussianCities()

    override fun getWeatherFromLocalStorageWorld(): List<Weather> = getWorldCities()


}