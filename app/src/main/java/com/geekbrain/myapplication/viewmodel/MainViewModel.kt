package com.geekbrain.myapplication.viewmodel

import android.content.SharedPreferences
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.geekbrain.myapplication.WeatherApplication
import com.geekbrain.myapplication.WeatherApplication.Companion.MY_LOCATION_PERMISSION
import com.geekbrain.myapplication.repository.*
import com.geekbrain.myapplication.viewmodel.AppState.*

@RequiresApi(Build.VERSION_CODES.N)
class MainViewModel(
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl.get(),
    private val locationRepository: LocationRepository = LocationRepository.get(),
) : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {


    private val TAG = "MainViewModel"

    private var locationPermission: MutableLiveData<Boolean> = MutableLiveData()

    private var weatherLiveData: MutableLiveData<AppState> = MutableLiveData()

    private var liveDataCurrentPointWeather =
        Transformations.switchMap(locationRepository.weatherCurrentPointStateLiveData)
                 { MutableLiveData<CurrentPointState>(locationRepository.weatherCurrentPointState)}



    fun getWeatherListLiveData(): MutableLiveData <AppState> {
        weatherLiveData.postValue(Success(weatherRepository.getWeatherFromRepository()))
        return weatherLiveData
    }

    fun getCurrentPointWeather() = liveDataCurrentPointWeather



    private val currentAddressLocation: MutableLiveData<String?> =
        locationRepository.mAddress

    private val currentLocation : MutableLiveData<Location?> =
        locationRepository.m1Location

    fun getCurrentAddressLocation() = currentAddressLocation

    init {
        //Log.i(TAG, "Count: ${localRepository.citiesCount()}")
        locationPermission.value = WeatherApplication.sharedPreferences
            .getBoolean(MY_LOCATION_PERMISSION, false)
        startMainViewModel()
        startLocationService()
    }

    fun startMainViewModel(){
            refreshDataFromRepository()
            weatherLiveData.postValue(Success(weatherRepository.getWeatherFromRepository()))
    }

    fun startLocationService(){
        if (locationPermission.value == true)
            locationRepository.startLocationService()
    }


     private fun refreshDataFromRepository() {
        weatherLiveData.postValue(Loading)
            try {
                weatherRepository.refreshWeatherList()

            } catch (e: Exception) {
                Log.i(TAG, "refreshDataFromRepositoryFailed: " + e.message)
                weatherLiveData.postValue(Error(e))
            }
    }

    fun getCurrentLocation() {
        locationRepository.getLocation()
    }



    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.i(TAG, "onSharedPreferenceChanged: key = $key")
        if (key == MY_LOCATION_PERMISSION
            && sharedPreferences?.getBoolean(MY_LOCATION_PERMISSION, false) == true) {
            locationRepository.startLocationService()
        }

    }
}

