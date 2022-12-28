package com.geekbrain.myapplication.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Weather(
    val city: City = getDefaultCity(),
    var weatherDTO: WeatherDTO?
) :Parcelable


fun getDefaultCity() = City("Saint-Petersburg", null, null  )

fun getWorldCities() = listOf(
        Weather(City("London", null, null), null ),
        Weather(City("Tokio", null, null), null),
        Weather(City("Paris", null, null), null),
    )


fun getRussianCities()= listOf(
        Weather(City("Moscow", null, null),null),
        Weather(City("Saint-Petersburg", null, null), null),
        Weather(City("Novosibirsk", null, null), null),
        Weather(City("Барнаул", null, null), null)
    )

