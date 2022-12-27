package com.geekbrain.myapplication.model

import com.google.gson.annotations.SerializedName


data class GeoObject (

  @SerializedName("Point"            ) var Point            : Point?            = Point()

)