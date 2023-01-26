package com.geekbrain.myapplication.model.weatherDTO

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Biomet (

  @SerializedName("index"     ) var index     : Int?    = null,
  @SerializedName("condition" ) var condition : String? = null

):Parcelable