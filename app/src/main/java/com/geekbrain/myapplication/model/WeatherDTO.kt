package com.geekbrain.myapplication.model
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WeatherDTO(
    @SerializedName("now")
    @Expose
    val now: Int?,

    @SerializedName("now_dt")
    @Expose
    val  nowDt: String,

    @SerializedName("info")
    @Expose
    val  info: Info,

    @SerializedName("fact")
    @Expose
    val fact: Fact?,

    @SerializedName("forecasts")
    @Expose
    val forecasts: List<Forecast?>,
)





/*data class FactDTO(
    val temp: Int?,
    val feels_like: Int?,
    val condition: String?,
    val wind_speed: Float?,
    val wind_gust: Float?,
    val wind_dir: String?,
    val pressure_mm: Int?,
    val humidity: Int?
)*/

/*data class ForecastsDTO(
    val date: String?,
    val parts: PartsDTO?,

)*/

/*data class PartsDTO(
    val night: PartDTO?,
    val morning: PartDTO?,
    val day: PartDTO?,
    val evening: PartDTO?,
    val day_short: PartDTO?,
    val night_short: PartShortDTO?,
)*/

/*data class PartDTO(
    val temp_min: Int?,
    val temp_max: Int?,
    val feels_like: Int?,
    val condition: String?,
)

data class PartShortDTO(
    val temp: Int?,
)*/
