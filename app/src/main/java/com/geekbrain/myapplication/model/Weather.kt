package com.geekbrain.myapplication.model

data class Weather(
    val city: City = getDefaultCity(),
    val temperature: Int = -7,
    val feelsLike: Int = -8
)

fun getDefaultCity() = City("Saint-Petersburg", 59.93750, 30.308611 )
