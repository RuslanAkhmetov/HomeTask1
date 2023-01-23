package com.geekbrain.myapplication.viewmodel

import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.geekbrain.myapplication.MainFragment
import com.geekbrain.myapplication.WeatherApplication
import com.geekbrain.myapplication.repository.*
import com.geekbrain.myapplication.viewmodel.AppState.*

@RequiresApi(Build.VERSION_CODES.N)
class MainViewModel(
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl.get(),
    private val locationRepository: LocationRepository = LocationRepository.get(),
) : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG = "MainViewModel"

    private var liveDataToObserve: MutableLiveData<AppState> = MutableLiveData()

    private var liveDataCurrentPointWeather =
        Transformations.switchMap(locationRepository.weatherCurrentPointStateLiveData)
                 { MutableLiveData<CurrentPointState>(locationRepository.weatherCurrentPointState) }

    fun getWeatherListLiveData(): MutableLiveData <AppState> {
        liveDataToObserve.postValue(Success(weatherRepository.getWeatherFromRepository()))
        return liveDataToObserve
    }

    fun getCurrentPointWeather() = liveDataCurrentPointWeather



    init {
        //Log.i(TAG, "Count: ${localRepository.citiesCount()}")
        startMainViewModel()
    }

    fun startMainViewModel(){
            locationRepository.startLocationService()
            refreshDataFromRepository()
            liveDataToObserve.postValue(Success(weatherRepository.getWeatherFromRepository()))
    }

     private fun refreshDataFromRepository() {
        liveDataToObserve.postValue(Loading)
            try {
                weatherRepository.refreshWeatherList()

            } catch (e: Exception) {
                Log.i(TAG, "refreshDataFromRepositoryFailed: " + e.message)
                liveDataToObserve.postValue(Error(e))
            }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.i(TAG, "onSharedPreferenceChanged: key = $key")
        if (key == MainFragment.MY_LOCATION_PERMISSION
            && sharedPreferences?.getBoolean(MainFragment.MY_LOCATION_PERMISSION, false) == true) {
            locationRepository.startLocationService()
        }

    }
}

