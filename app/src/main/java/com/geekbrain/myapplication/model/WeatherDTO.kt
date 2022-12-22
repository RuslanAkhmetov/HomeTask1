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


