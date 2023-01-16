package com.geekbrain.myapplication.model.weatherDTO

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Info (
    @SerializedName("lat")
    @Expose
    var lat: Float? = null,

    @SerializedName("lon")
    @Expose
    var lon: Float? = null,

    @SerializedName("url")
    @Expose
    var url: String? = null,
) : Parcelable