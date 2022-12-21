package com.geekbrain.myapplication.model

data class WeatherDTO(
    val fact: FactDTO?,
    val forecasts: List<ForecastsDTO?>,
)

data class FactDTO(
    val temp: Int?,
    val feels_like: Int?,
    val condition: String?,
    val wind_speed: Float?,
    val wind_gust: Float?,
    val wind_dir: String?,
    val pressure_mm: Int?,
    val humidity: Int?
)

data class ForecastsDTO(
    val date: String?,
    val parts: PartsDTO?,

)

data class PartsDTO(
    val night: PartDTO?,
    val morning: PartDTO?,
    val day: PartDTO?,
    val evening: PartDTO?,
    val day_short: PartDTO?,
    val night_short: PartShortDTO?,
)

data class PartDTO(
    val temp_min: Int?,
    val temp_max: Int?,
    val feels_like: Int?,
    val condition: String?,
)

data class PartShortDTO(
    val temp: Int?,
)
