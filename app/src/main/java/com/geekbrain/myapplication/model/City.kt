package com.geekbrain.myapplication.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

data class City(
    val city: String?,
    val isRus: Boolean?,
    var lat: Float?,
    var lon: Float?
): Parcelable {
    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readBoolean(),
        parcel.readFloat(),
        parcel.readFloat()
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(city)
        isRus?.let { parcel.writeBoolean(it) }
        lat?.let { parcel.writeFloat(it) }
        lon?.let { parcel.writeFloat(it) }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<City> {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createFromParcel(parcel: Parcel): City {
            return City(parcel)
        }

        override fun newArray(size: Int): Array<City?> {
            return arrayOfNulls(size)
        }
    }

    fun getWorldCities() = listOf(
        City("London", false, 51.5085300F, -0.1257400F),
        City("Tokio", false,35.6895000F, 139.6917100F),
        City("Paris", false,48.853410F, 2.3488000F),
    )


    fun getRussianCities()= listOf(
        City("Moscow", true,55.755825F, 37.6173f),
        City("Saint-Petersburg", true,59.93750F, 30.30861F),
        City("Novosibirsk", true,55.008354F, 82.93573F),
    )
}
