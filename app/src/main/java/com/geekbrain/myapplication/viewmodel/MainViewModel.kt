package com.geekbrain.myapplication.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geekbrain.myapplication.repository.Repository
import com.geekbrain.myapplication.repository.WeatherRepository
import java.lang.Thread.sleep
import java.util.Random

class MainViewModel (
    private val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData(),
    private val repository: Repository = WeatherRepository()
) : ViewModel() {

    fun getLiveData() = liveDataToObserve

    @RequiresApi(Build.VERSION_CODES.N)
    fun getWeather() = getDataFromLocalSource()

    @RequiresApi(Build.VERSION_CODES.N)
    fun getWeatherFromRemoteSource() = getDataFromLocalSource()

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getDataFromLocalSource(){
        liveDataToObserve.value = AppState.Loading
        val random = Random()
        random.ints()
        Thread{
            sleep(2000)
            if (random.nextBoolean()) {
                liveDataToObserve.postValue(AppState.Success(repository.getWeatherFromLocalStorage()))
            } else {
                liveDataToObserve.postValue(AppState.Error(java.lang.RuntimeException("Loadingdata Error")))
            }
        }.start()
    }
}