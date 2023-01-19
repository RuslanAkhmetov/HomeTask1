package com.geekbrain.myapplication.model

import android.os.Parcelable
import com.geekbrain.myapplication.model.weatherDTO.WeatherDTO
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Weather(
    var city: City = getDefaultCity(),
    var weatherDTO: WeatherDTO?
) : Parcelable


fun getDefaultCity() = City("Saint-Petersburg", true, null, null)


fun initWeatherList() = listOf(
    Weather(City("Moscow", true, null, null), null),
    Weather(City("Saint-Petersburg", true, null, null), null),
    Weather(City("Novosibirsk", true, null, null), null),
    Weather(City("Барнаул", true, null, null), null),
    Weather(City("London", false, null, null), null),
    Weather(City("Tokio",false, null, null), null),
    Weather(City("Paris",false, null, null), null),
)

