package com.geekbrain.myapplication.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Info {
    @SerializedName("lat")
    @Expose
    var lat: Float? = null

    @SerializedName("lon")
    @Expose
    var lon: Float? = null

    @SerializedName("url")
    @Expose
    var url: String? = null
}