package com.geekbrain.myapplication.viewmodel

import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.geekbrain.myapplication.WeatherApplication
import com.geekbrain.myapplication.WeatherApplication.Companion.MY_LOCATION_PERMISSION
import com.geekbrain.myapplication.model.Weather
import com.geekbrain.myapplication.repository.*
import com.geekbrain.myapplication.viewmodel.AppState.*

@RequiresApi(Build.VERSION_CODES.S)
class MainViewModel(
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl.get(),
    private val locationRepository: LocationRepository = LocationRepository.get(),
) : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {


    private val TAG = "mainViewModel"

    private var locationPermission: MutableLiveData<Boolean> = MutableLiveData()

    private var weatherLiveData: MutableLiveData<AppState>  =
        Transformations.switchMap(weatherRepository.listWeatherLiveDataFromRepo)
        {MutableLiveData<AppState>(Success(weatherRepository
            .listWeatherLiveDataFromRepo.value as MutableList<Weather>))} as MutableLiveData<AppState>




    private var liveDataCurrentPointWeather  =
        Transformations.switchMap(locationRepository.weatherCurrentPointStateLiveData)
                 { MutableLiveData<CurrentPointState>(locationRepository.weatherCurrentPointState)}



   fun getWeatherListLiveData(): MutableLiveData <AppState> {
       weatherRepository.refreshWeatherList()
        weatherLiveData.postValue(Success(weatherRepository.getWeatherFromRepository()))
        return weatherLiveData
    }

    fun getCurrentPointWeather() = liveDataCurrentPointWeather



    init {
        //Log.i(TAG, "Count: ${localRepository.citiesCount()}")
        locationPermission.value = WeatherApplication.sharedPreferences
            .getBoolean(MY_LOCATION_PERMISSION, false)
        startMainViewModel()
        getCurrentLocation()
    }

    fun startMainViewModel(){
            refreshDataFromRepository()
            weatherLiveData.postValue(Success(weatherRepository.getWeatherFromRepository()))
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

    @RequiresApi(Build.VERSION_CODES.S)
    fun getCurrentLocation() {
        locationRepository.getLocation()
    }



    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.i(TAG, "onSharedPreferenceChanged: key = $key")
        if (key == MY_LOCATION_PERMISSION
            && sharedPreferences?.getBoolean(MY_LOCATION_PERMISSION, false) == true) {
            locationRepository.getLocation()
        }

    }
}

