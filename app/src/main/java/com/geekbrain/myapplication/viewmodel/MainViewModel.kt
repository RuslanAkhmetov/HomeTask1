package com.geekbrain.myapplication.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekbrain.myapplication.repository.Repository
import com.geekbrain.myapplication.repository.WeatherRepository
import com.geekbrain.myapplication.viewmodel.AppState.*
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class MainViewModel(
    private var liveDataToObserve: MutableLiveData<AppState> = MutableLiveData(),
    private val weatherRepository: Repository = WeatherRepository()
) : ViewModel() {

    private val TAG = "MainViewModel"

    fun getLiveData() = liveDataToObserve

    private val weatherList = weatherRepository.getWeatherFromRepository()

    init {
        refreshDataFromRepository()
    }

    fun refreshDataFromRepository() {
        liveDataToObserve.postValue(Loading)
        viewModelScope.launch {
            try {
                weatherRepository.refreshWeatherList()
                liveDataToObserve.postValue(Success(weatherList))
            } catch (e: Exception) {
                if (liveDataToObserve.value == null) {
                    Log.i(TAG, "refreshDataFromRepository: " + e.message)
                    liveDataToObserve.postValue(Error(e))
                }
            }
        }
    }


}