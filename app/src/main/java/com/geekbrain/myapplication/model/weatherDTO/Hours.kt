package com.geekbrain.myapplication.model.weatherDTO

import com.google.gson.annotations.SerializedName


data class Hours (

  @SerializedName("hour"          ) var hour         : String?  = null,
  @SerializedName("hour_ts"       ) var hourTs       : Int?     = null,
  @SerializedName("temp"          ) var temp         : Int?     = null,
  @SerializedName("feels_like"    ) var feelsLike    : Int?     = null,
  @SerializedName("icon"          ) var icon         : String?  = null,
  @SerializedName("condition"     ) var condition    : String?  = null,
  @SerializedName("cloudness"     ) var cloudness    : Float?     = null,
  @SerializedName("prec_type"     ) var precType     : Int?     = null,
  @SerializedName("prec_strength" ) var precStrength : Float?     = null,
  @SerializedName("is_thunder"    ) var isThunder    : Boolean? = null,
  @SerializedName("wind_dir"      ) var windDir      : String?  = null,
  @SerializedName("wind_speed"    ) var windSpeed    : Double?  = null,
  @SerializedName("wind_gust"     ) var windGust     : Double?  = null,
  @SerializedName("pressure_mm"   ) var pressureMm   : Int?     = null,
  @SerializedName("pressure_pa"   ) var pressurePa   : Int?     = null,
  @SerializedName("humidity"      ) var humidity     : Int?     = null,
  @SerializedName("uv_index"      ) var uvIndex      : Int?     = null,
  @SerializedName("soil_temp"     ) var soilTemp     : Int?     = null,
  @SerializedName("soil_moisture" ) var soilMoisture : Float?  = null,
  @SerializedName("prec_mm"       ) var precMm       : Float?     = null,
  @SerializedName("prec_period"   ) var precPeriod   : Int?     = null,
  @SerializedName("prec_prob"     ) var precProb     : Int?     = null

)