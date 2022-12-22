package com.geekbrain.myapplication.model

import com.google.gson.annotations.SerializedName


data class Biomet (

  @SerializedName("index"     ) var index     : Int?    = null,
  @SerializedName("condition" ) var condition : String? = null

)