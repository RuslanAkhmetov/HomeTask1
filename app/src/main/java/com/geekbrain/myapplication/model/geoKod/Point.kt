package com.geekbrain.myapplication.model.geoKod

import com.google.gson.annotations.SerializedName


data class Point (

  @SerializedName("pos" ) var pos : String? = null

)