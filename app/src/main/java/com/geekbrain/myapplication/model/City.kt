package com.geekbrain.myapplication.model

import android.os.Parcel
import android.os.Parcelable

data class City(
    val city: String?,
    var lat: Float?,
    var lon: Float?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readFloat(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(city)

        lat?.let { parcel.writeFloat(it) }
        lon?.let { parcel.writeFloat(it) }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<City> {
        override fun createFromParcel(parcel: Parcel): City {
            return City(parcel)
        }

        override fun newArray(size: Int): Array<City?> {
            return arrayOfNulls(size)
        }
    }

    fun getWorldCities() = listOf(
        City("London", 51.5085300F, -0.1257400F),
        City("Tokio", 35.6895000F, 139.6917100F),
        City("Paris", 48.853410F, 2.3488000F),
    )


    fun getRussianCities()= listOf(
        City("Moscow", 55.755825F, 37.6173f),
        City("Saint-Petersburg", 59.93750F, 30.30861F),
        City("Novosibirsk", 55.008354F, 82.93573F),
    )
}
