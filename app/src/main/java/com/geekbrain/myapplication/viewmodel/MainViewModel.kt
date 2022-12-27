package com.geekbrain.myapplication.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geekbrain.myapplication.model.Weather
import com.geekbrain.myapplication.repository.Repository
import com.geekbrain.myapplication.repository.WeatherRepository
import java.lang.Thread.sleep
import java.util.Random

class MainViewModel(
    private var liveDataToObserve: MutableLiveData<AppState> = MutableLiveData(),
    private val repository: Repository = WeatherRepository()
) : ViewModel() {

    private val TAG = "MainViewModel"

    fun getLiveData() = liveDataToObserve

    @RequiresApi(Build.VERSION_CODES.N)
    fun getWeather(isRus: Boolean) {
        getDataFromLocalSource(isRus)
        getWeatherFromRemoteSource()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getWeatherFromRemoteSource() {
        Log.i(TAG, "getWeatherFromRemoteSource: "+  liveDataToObserve.value!!::class.java)
        if (liveDataToObserve.value is AppState.Success) {
            try {
                liveDataToObserve.setValue(
                    AppState.Success(
                        repository.getWeatherFromServer((liveDataToObserve.value as AppState.Success)
                            .weatherData)))
            } catch (e: Exception) {
                liveDataToObserve.setValue(
                    AppState.Error(
                        java.lang.RuntimeException("Loading data from Server Error" + e.message)))
            }
        } else {
            liveDataToObserve.setValue(
                AppState.Error(
                    java.lang.RuntimeException("Loading data from Server Error: weatherData should be initialized")))
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun getWeatherFromLocalSourceRus() = getDataFromLocalSource(true)

    @RequiresApi(Build.VERSION_CODES.N)
    fun getWeatherFromLocalSourceWorld() = getDataFromLocalSource(false)

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getDataFromLocalSource(isRus: Boolean = true) {
       // liveDataToObserve.value = AppState.Loading

        //Thread {
            try{
                liveDataToObserve.setValue(
                    AppState.Success(
                        if (isRus) {
                            repository.getWeatherFromLocalStorageRus()
                        } else {
                            repository.getWeatherFromLocalStorageWorld()
                        }
                    )
                )
                Log.i(TAG, "getDataFromLocalSource: " + liveDataToObserve.value!!::class.java)
                } catch (e: Exception){
                    liveDataToObserve.postValue(AppState.Error(e))
                Log.i(TAG, "getDataFromLocalSource: Loading from local resource Failed: $e")
            }
      //  }.start()
    }
}