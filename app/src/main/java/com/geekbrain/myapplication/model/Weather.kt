package com.geekbrain.myapplication.model

import android.os.Parcel
import android.os.Parcelable

data class Weather(
    val city: City? = getDefaultCity(),
    val temperature: Int = -7,
    val feelsLike: Int = -8
) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(City::class.java.classLoader),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Weather> {
        override fun createFromParcel(parcel: Parcel): Weather {
            return Weather(parcel)
        }

        override fun newArray(size: Int): Array<Weather?> {
            return arrayOfNulls(size)
        }
    }
}

fun getDefaultCity() = City("Saint-Petersburg", 59.93750, 30.308611 )

fun getWorldCities() = listOf(
        Weather(City("London", 51.5085300, -0.1257400), 1, 2),
        Weather(City("Tokio", 35.6895000, 139.6917100), 3, 4),
        Weather(City("Paris", 48.853410, 2.3488000), 5,5)
    )


fun getRussianCities()= listOf(
        Weather(City("Moscow", 55.755826, 37.6172999),1,2),
        Weather(City("Saint-Petersburg", 59.93750, 30.308611 ), 3,1),
        Weather(City("Novosibirsk", 55.008352599999, 82.9357327000), 5, 6)
    )

