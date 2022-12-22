package com.geekbrain.myapplication.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Weather(
    val city: City = getDefaultCity(),
    val temperature: Int = -7,
    val feelsLike: Int = -8
) :Parcelable



fun getDefaultCity() = City("Saint-Petersburg", 59.93750F, 30.308611F )

fun getWorldCities() = listOf(
        Weather(City("London", 51.5085300F, -0.1257400F), 1, 2),
        Weather(City("Tokio", 35.6895000F, 139.6917100F), 3, 4),
        Weather(City("Paris", 48.853410F, 2.3488000F), 5,5)
    )


fun getRussianCities()= listOf(
        Weather(City("Moscow", 55.755825F, 37.6173f),1,2),
        Weather(City("Saint-Petersburg", 59.93750F, 30.30861F), 3,1),
        Weather(City("Novosibirsk", 55.008354F, 82.93573F), 5, 6)
    )

