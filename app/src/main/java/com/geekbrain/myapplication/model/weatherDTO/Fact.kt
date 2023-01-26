package com.geekbrain.myapplication.model.weatherDTO

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class Fact (
    @SerializedName("temp")
    @Expose
    var temp: Int? = null,

    @SerializedName("feels_like")
    @Expose
    var feelsLike: Int? = null,

    @SerializedName("icon")
    @Expose
    var icon: String? = null,

    @SerializedName("condition")
    @Expose
    var condition: String? = null,

    @SerializedName("wind_speed")
    @Expose
    var windSpeed: Float? = null,

    @SerializedName("wind_gust")
    @Expose
    var windGust: Float? = null,

    @SerializedName("wind_dir")
    @Expose
    var windDir: String? = null,

    @SerializedName("pressure_mm")
    @Expose
    var pressureMm: Int? = null,

    @SerializedName("pressure_pa")
    @Expose
    var pressurePa: Int? = null,

    @SerializedName("humidity")
    @Expose
    var humidity: Int? = null,

    @SerializedName("daytime")
    @Expose
    var daytime: String? = null,

    @SerializedName("polar")
    @Expose
    var polar: Boolean? = null,

    @SerializedName("season")
    @Expose
    var season: String? = null,

    @SerializedName("obs_time")
    @Expose
    var obsTime: Int? = null,
) : Parcelable