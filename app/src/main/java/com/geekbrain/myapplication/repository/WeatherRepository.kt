package com.geekbrain.myapplication.repository

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.widget.Toast
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.getSystemServiceName
import com.geekbrain.myapplication.model.*
import com.geekbrain.myapplication.viewmodel.CoordinatesLoader
import com.geekbrain.myapplication.viewmodel.WeatherLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



class WeatherRepository private constructor(appContext: Context) : Repository {   //context Application

    private val TAG = "WeatherRepository"

    private var listWeatherReceived: MutableList<Weather> = mutableListOf()

    lateinit var locationManager: LocationManager

    private val locationPermissionCode = 2

    override fun getWeatherFromRepository(): MutableList<Weather> = listWeatherReceived

    companion object{
        private var instance: WeatherRepository? = null
        fun initialize(context: Context){
            if (instance == null)
                instance = WeatherRepository(context)
        }

        fun get(): WeatherRepository{
            return instance?: throw IllegalStateException("MovieRepository must be initialized")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override suspend fun refreshWeatherList() {

        withContext(Dispatchers.IO) {
            try {
                //Add weather for current position
                getWeatherForCurrentPosition()
                getWeatherFromServer(getWeatherFromLocalStorageRus() + getWeatherFromLocalStorageWorld())
            } catch (e: Exception){
                throw e
            }

        }
    }

    override fun getWeatherForCurrentPosition() {

    }

    private fun getLocation(appContext: Context) {
        locationManager =  appContext.getSystemService(LOCATION_SERVICE) as LocationManager
        //locationManager =
          //  getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                .. ,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,5000,
            500F,this)
    }



    override fun onLocationChanged(location: Location) {
        TODO("Not yet implemented")
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