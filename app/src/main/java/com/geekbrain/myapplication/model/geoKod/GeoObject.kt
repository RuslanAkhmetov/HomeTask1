package com.geekbrain.myapplication.model.geoKod

import com.geekbrain.myapplication.model.geoKod.Point
import com.google.gson.annotations.SerializedName


data class GeoObject (
  @SerializedName("name"             ) var name             : String?           = null,
  @SerializedName("Point"            ) var Point            : Point?            = Point()

)