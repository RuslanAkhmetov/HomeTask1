package com.geekbrain.myapplication.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geekbrain.myapplication.repository.Repository
import com.geekbrain.myapplication.repository.WeatherRepository
import java.lang.Thread.sleep
import java.util.Random

class MainViewModel(
    private val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData(),
    private val repository: Repository = WeatherRepository()
) : ViewModel() {

    fun getLiveData() = liveDataToObserve

    @RequiresApi(Build.VERSION_CODES.N)
    fun getWeather() = getDataFromLocalSource(true)

    @RequiresApi(Build.VERSION_CODES.N)
    fun getWeatherFromRemoteSource() = getDataFromLocalSource(true)

    @RequiresApi(Build.VERSION_CODES.N)
    fun getWeatherFromLocalSourceRus() = getDataFromLocalSource(true)

    @RequiresApi(Build.VERSION_CODES.N)
    fun getWeatherFromLocalSourceWorld() = getDataFromLocalSource(false)

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getDataFromLocalSource(isRus: Boolean = true) {
        liveDataToObserve.value = AppState.Loading
        val random = Random()
        random.ints()
        Thread {
            sleep(2000)
            if (true) {          //random.nextBoolean()
                liveDataToObserve.postValue(
                    AppState.Success(
                        if (isRus) {
                            repository.getWeatherFromLocalStorageRus()
                        } else {
                            repository.getWeatherFromLocalStorageWorld()
                        }
                    )
                )
            } else {
                liveDataToObserve.postValue(AppState.Error(java.lang.RuntimeException("Loading data Error")))
            }
        }.start()
    }
}