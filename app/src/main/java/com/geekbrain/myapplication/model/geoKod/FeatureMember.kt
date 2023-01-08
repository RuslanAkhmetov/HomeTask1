package com.geekbrain.myapplication.model.geoKod

import com.geekbrain.myapplication.model.geoKod.GeoObject
import com.google.gson.annotations.SerializedName


data class featureMember (

  @SerializedName("GeoObject" ) var GeoObject : GeoObject? = GeoObject()

) {
  override fun toString(): String {
    return "featureMember(GeoObject=$GeoObject)"
  }
}