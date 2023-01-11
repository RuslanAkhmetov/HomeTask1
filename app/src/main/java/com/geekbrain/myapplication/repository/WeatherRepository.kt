package com.geekbrain.myapplication.repository

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.geekbrain.myapplication.model.*
import java.util.Collections


class WeatherRepository private constructor(_context: Context) : Repository { //context Application

    private val TAG = "WeatherRepository"

    val context = _context

    private var listWeatherSent: MutableList<Weather> = mutableListOf()
    private var listWeatherReceived: MutableList<Weather> =
        Collections.synchronizedList(mutableListOf())


    override fun getWeatherFromRepository(): MutableList<Weather> = listWeatherReceived

    companion object {
        private var instance: WeatherRepository? = null
        fun initialize(context: Context) {
            if (instance == null)
                instance = WeatherRepository(context)
        }

        fun get(): WeatherRepository {
            return instance ?: throw IllegalStateException("MovieRepository must be initialized")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun refreshWeatherList() {
        //withContext(Dispatchers.IO) {
                try {
                    listWeatherSent = (getWeatherFromLocalStorageRus() + getWeatherFromLocalStorageWorld()) as MutableList<Weather>
                    getWeatherFromServer(listWeatherSent)

                } catch (e: Exception) {
                    throw e
                }
        //}
    }

    /*@RequiresApi(Build.VERSION_CODES.N)
    override suspend fun getWeatherForCurrentPosition() {
        withContext(Dispatchers.IO) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            if (context.checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    context, "Application require Location Permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
            val weatherCurrentPoint = Weather(City("CurrentPoint", true, null, null), null)
            fusedLocationClient.lastLocation
                .addOnSuccessListener {
                    Log.i(TAG, "getWeatherForCurrentPosition: $it")
                    weatherCurrentPoint.city.lat = it?.latitude?.toFloat()
                    weatherCurrentPoint.city.lon =
                        it?.longitude?.toFloat()
                    listWeatherSent.add(weatherCurrentPoint)
                    listWeatherSent += getWeatherFromLocalStorageRus() + getWeatherFromLocalStorageWorld()

                }

            Log.i(TAG, "getWeatherForCurrentPosition: $weatherCurrentPoint")

            // return weatherCurrentPoint

        }
    }*/



        private val coordinatesLoaderListener =
            object : CoordinatesLoader.CoordinateLoaderListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onLoaded(city: City) {
                    WeatherLoader(onWeatherLoaderListener, city).apply {
                        loaderWeather()
                    }
                }

                override fun onFailed(throwable: Throwable) {
                    Log.i(TAG, "CoordinatesLoaderFailed: " + throwable.message)
                    throw throwable
                }

            }

        private val onWeatherLoaderListener: WeatherLoader.WeatherLoaderListener =
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
            synchronized(listWeather) {
                Log.i(TAG, "getWeatherFromServer size of listweather: ${listWeather.size}")
                for (weatherItem in listWeather) {
                    try {
                        if (weatherItem.city.lat == null || weatherItem.city.lon == null) {
                            CoordinatesLoader(coordinatesLoaderListener, weatherItem.city)
                                .also {
                                    it.getCoordinates()
                                }
                        } else {
                            val loader = WeatherLoader(onWeatherLoaderListener, weatherItem.city)
                            Log.i(TAG, "getWeatherFromServer: ${weatherItem.city}")
                            loader.loaderWeather()
                        }
                    } catch (e: Exception) {
                        Log.i(TAG, "getWeatherFromServerFailed: " + e.message)
                        throw e
                    }
                }
                Log.i(TAG, "getWeatherFromServer: listWeatherReceived " + listWeatherReceived.size)
            }
        }

        @RequiresApi(Build.VERSION_CODES.N)
        fun getWeatherFromServer(weather: Weather) {
            synchronized(listWeatherReceived) {
                try {
                    val loader = WeatherLoader(onWeatherLoaderListener, weather.city)
                    Log.i(TAG, "getWeatherFromServer: ${weather.city}")
                    loader.loaderWeather()

                } catch (e: Exception) {
                    Log.i(TAG, "getWeatherFromServerFailed: " + e.message)
                    throw e
                }
            }

        }

        override fun getWeatherFromLocalStorageRus(): List<Weather> = getRussianCities()

        override fun getWeatherFromLocalStorageWorld(): List<Weather> = getWorldCities()


    }