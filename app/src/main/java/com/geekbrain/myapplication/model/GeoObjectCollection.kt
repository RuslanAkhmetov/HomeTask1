package com.geekbrain.myapplication.model

import com.google.gson.annotations.SerializedName


data class GeoObjectCollection (

  @SerializedName("featureMember"    ) var featureMember    : ArrayList<featureMember> = arrayListOf()

) {
  override fun toString(): String {
    return "GeoObjectCollection(featureMember=$featureMember)"
  }
}