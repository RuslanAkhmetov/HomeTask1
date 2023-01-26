package com.geekbrain.myapplication.model.weatherDTO

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GeoObject (

  @SerializedName("district" )
  @Expose
  var district : District? = District(),

  @SerializedName("locality" )
  @Expose
  var locality : Locality? = Locality(),

  @SerializedName("province" )
  @Expose
  var province : Province? = Province(),

  @SerializedName("country"  )
  @Expose
  var country  : Country?  = Country()

): Parcelable