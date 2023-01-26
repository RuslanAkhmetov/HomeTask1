package com.geekbrain.myapplication.model.weatherDTO
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherDTO(
    @SerializedName("now")
    @Expose
    val now: Int?,

    @SerializedName("now_dt")
    @Expose
    val  nowDt: String,

    @SerializedName("info"       )
    var info      : Info,

    @SerializedName("geo_object" )
    @Expose
    var geoObject : GeoObject?,

    @SerializedName("fact")
    @Expose
    val fact: Fact?,

    @SerializedName("forecasts")
    @Expose
    val forecasts: List<Forecast?>,
): Parcelable


