package com.geekbrain.myapplication.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Weather(
    val city: City = getDefaultCity(),
    var weatherDTO: WeatherDTO?
) : Parcelable


fun getDefaultCity() = City("Saint-Petersburg", true, null, null)

fun getWorldCities() = listOf(
    Weather(City("London", false, null, null), null),
    Weather(City("Tokio",false, null, null), null),
    Weather(City("Paris",false, null, null), null),
)


fun getRussianCities() = listOf(
    Weather(City("Moscow", true, null, null), null),
    Weather(City("Saint-Petersburg", true, null, null), null),
    Weather(City("Novosibirsk", true, null, null), null),
    Weather(City("Барнаул", true, null, null), null)
)

