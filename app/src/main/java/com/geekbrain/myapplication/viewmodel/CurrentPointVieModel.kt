package com.geekbrain.myapplication.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.geekbrain.myapplication.model.City
import com.geekbrain.myapplication.model.Weather
import com.geekbrain.myapplication.repository.ACTION_BROADCAST
import com.geekbrain.myapplication.repository.EXTRA_LOCATION
import com.geekbrain.myapplication.repository.LocationRepository
import com.geekbrain.myapplication.repository.WeatherLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CurrentPointVieModel(
    private val locationRepository: LocationRepository = LocationRepository.get(),
) : ViewModel() {
    private val TAG = CurrentPointVieModel::class.java.simpleName

    private var currentPointLocation: Location? = null

    private var currentPointWeather: Weather = Weather(
        City(
            "Current Point", true,
            currentPointLocation?.latitude?.toFloat(),
            currentPointLocation?.longitude?.toFloat()
        ),
        null
    )

    private var liveDataCurrentPointWeather: MutableLiveData<CurrentPointState> =
        MutableLiveData()

    private var loadingError: Exception? = null

    private var myReceiver = MyReceiver()

    // The BroadcastReceiver used to listen from broadcasts from the service.

    init {
        Log.i(TAG, "init CurrentPointVieModel: ")
        locationRepository.startLocationService()

        LocalBroadcastManager.getInstance(locationRepository.getRepositoryContext())
            .registerReceiver(
                this.myReceiver, IntentFilter(
                    ACTION_BROADCAST
                )
            )
    }

    fun getCurrentPointWeather(): MutableLiveData<CurrentPointState> {
        if (currentPointWeather.weatherDTO != null) {
            liveDataCurrentPointWeather.value =
                CurrentPointState.Success(currentPointWeather)
        } else if (loadingError != null) {
            liveDataCurrentPointWeather.value =
                CurrentPointState.Error(loadingError!!)
        } else {
            liveDataCurrentPointWeather.value =
                CurrentPointState.Loading
        }
        return liveDataCurrentPointWeather
    }

    private val onWeatherLoaderListener: WeatherLoader.WeatherLoaderListener =
        object : WeatherLoader.WeatherLoaderListener {
            override fun onLoaded(weather: Weather) {
                viewModelScope.launch {
                    currentPointWeather = weather
                    liveDataCurrentPointWeather.value =
                        CurrentPointState.Success(currentPointWeather)
                }
            }

            override fun onFailed(throwable: Throwable) {
                Log.i(TAG, "weatherLoaderFailed: " + throwable.message)
                throw throwable
            }
        }

    private val onLocationLoaderListener: LocationRepository.LocationLoaderListener =
        object : LocationRepository.LocationLoaderListener {
            override fun onLoaded(location: Location) {
                currentPointLocation = location

            }
        }


    private inner class MyReceiver : BroadcastReceiver() {

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(context: Context?, intent: Intent?) {
            currentPointLocation = intent?.getParcelableExtra<Location>(
                EXTRA_LOCATION
                //Location::class.java
            )


                    val loaderWeather = WeatherLoader(
                        onWeatherLoaderListener,
                        City(
                            "Current Point",
                            true,
                            currentPointLocation?.latitude?.toFloat(),
                            currentPointLocation?.longitude?.toFloat()
                        )
                    )
                    loaderWeather.loaderWeather()


            Log.i(TAG, "onReceive: $currentPointLocation")

        }

    }


}
