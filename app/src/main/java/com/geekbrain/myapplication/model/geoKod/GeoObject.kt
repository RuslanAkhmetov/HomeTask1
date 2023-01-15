package com.geekbrain.myapplication.model.geoKod

import com.google.gson.annotations.SerializedName


data class GeoObject (
  @SerializedName("name"             ) var name             : String?           = null,
  @SerializedName("description"      ) var description      : String?           = null,
  @SerializedName("Point"            ) var Point            : Point?            = Point()

)