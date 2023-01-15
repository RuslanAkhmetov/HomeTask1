package com.geekbrain.myapplication.model.weatherDTO

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Part {
    @SerializedName("part_name")
    @Expose
    var partName: String? = null

    @SerializedName("temp_min")
    @Expose
    var tempMin: Int? = null

    @SerializedName("temp_max")
    @Expose
    var tempMax: Int? = null

    @SerializedName("temp_avg")
    @Expose
    var tempAvg: Int? = null

    @SerializedName("feels_like")
    @Expose
    var feelsLike: Int? = null

    @SerializedName("icon")
    @Expose
    var icon: String? = null

    @SerializedName("condition")
    @Expose
    var condition: String? = null

    @SerializedName("daytime")
    @Expose
    var daytime: String? = null

    @SerializedName("polar")
    @Expose
    var polar: Boolean? = null

    @SerializedName("wind_speed")
    @Expose
    var windSpeed: Float? = null

    @SerializedName("wind_gust")
    @Expose
    var windGust: Float? = null

    @SerializedName("wind_dir")
    @Expose
    var windDir: String? = null

    @SerializedName("pressure_mm")
    @Expose
    var pressureMm: Int? = null

    @SerializedName("pressure_pa")
    @Expose
    var pressurePa: Int? = null

    @SerializedName("humidity")
    @Expose
    var humidity: Int? = null

    @SerializedName("prec_mm")
    @Expose
    var precMm: Int? = null

    @SerializedName("prec_period")
    @Expose
    var precPeriod: Int? = null

    @SerializedName("prec_prob")
    @Expose
    var precProb: Int? = null
}