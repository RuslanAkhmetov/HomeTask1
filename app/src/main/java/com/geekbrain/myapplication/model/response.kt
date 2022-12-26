package com.geekbrain.myapplication.model

import com.google.gson.annotations.SerializedName


data class response (

  @SerializedName("GeoObjectCollection" ) var GeoObjectCollection : GeoObjectCollection? = GeoObjectCollection()

) {
  override fun toString(): String {
    return "response(GeoObjectCollection=$GeoObjectCollection)"
  }
}