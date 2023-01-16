package com.geekbrain.myapplication.viewmodel

import android.content.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.geekbrain.myapplication.model.Weather
import com.geekbrain.myapplication.repository.*
import com.geekbrain.myapplication.viewmodel.AppState.*
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class MainViewModel(
    private val weatherRepository: Repository = WeatherRepository.get(),
    private val locationRepository: LocationRepository = LocationRepository.get(),
) : ViewModel() {

    private val TAG = "MainViewModel"

    private var liveDataToObserve: MutableLiveData<AppState> = MutableLiveData()

    private var liveDataCurrentPointWeather =
        Transformations.switchMap(locationRepository.weatherCurrentPointStateLiveData)
                 { MutableLiveData<CurrentPointState>(locationRepository.weatherCurrentPointState) }

    fun getWeatherListLiveData() = liveDataToObserve

    fun getCurrentPointWeather() = liveDataCurrentPointWeather

    private var weatherList: MutableList<Weather> =
                    weatherRepository.getWeatherFromRepository()

    init {
        startMainViewModel()
    }

    fun startMainViewModel(){
        viewModelScope.launch {
            locationRepository.startLocationService()
            refreshDataFromRepository()
            liveDataToObserve.postValue(Success(weatherList))
        }
    }

     private fun refreshDataFromRepository() {
        liveDataToObserve.postValue(Loading)
       // withContext(Dispatchers.IO) {
            try {
                weatherRepository.refreshWeatherList()
            } catch (e: Exception) {
                Log.i(TAG, "refreshDataFromRepositoryFailed: " + e.message)
                liveDataToObserve.postValue(Error(e))
            }
       // }
    }
}

