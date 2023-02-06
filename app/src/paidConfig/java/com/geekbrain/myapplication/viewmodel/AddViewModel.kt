package com.geekbrain.myapplication.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.geekbrain.myapplication.repository.LocationRepository
import com.geekbrain.myapplication.repository.WeatherRepository
import com.geekbrain.myapplication.repository.WeatherRepositoryImpl

class AddViewModel(
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl.get(),
    private val locationRepository: LocationRepository = LocationRepository.get()
) : ViewModel() {

    private val TAG = "AddViewModel"

    private var cityNewWeatherLiveData =
        Transformations.switchMap(locationRepository.weatherNewPointStateLiveData)
        { MutableLiveData<CurrentPointState>(locationRepository.weatherNewPositionCurrentPointState) }

    fun getCityNewWeatherLiveData() = cityNewWeatherLiveData

    @RequiresApi(Build.VERSION_CODES.Q)
    fun searchCity(cityName: String) {
        locationRepository.getFullCityName(cityName)
        Log.i(TAG, "searchCity: ${locationRepository.weatherNewPositionCurrentPointState}")

    }

    fun addCityToDB() {
        if (cityNewWeatherLiveData.value is CurrentPointState.Success) {
            val city = (cityNewWeatherLiveData.value as CurrentPointState.Success)
                .weatherInCurrentPoint?.city
            if (city != null) {
                weatherRepository.saveCityToDB(city)
            }
            locationRepository.requestWeatherDTOForNewPosition()

        }
    }


}