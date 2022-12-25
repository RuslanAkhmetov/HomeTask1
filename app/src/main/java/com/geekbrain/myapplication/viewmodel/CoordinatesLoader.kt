package com.geekbrain.myapplication.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.geekbrain.myapplication.BuildConfig
import com.geekbrain.myapplication.model.WeatherDTO
import java.io.BufferedReader
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors

class CoordinatesLoader(
    val listener: CoordinatesLoader.CoordinateLoaderListener,
    val adress: String,
) {
    private val TAG = "CoordinatesLoader"

    interface CoordinateLoaderListener {
        fun onLoaded(lat: Double, lon: Double)
        fun onFailed(throwable: Throwable)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getCoordinates(adress: String) =
        try{
            val uri = URL("https://geocode-maps.yandex.ru/1.x/"
                    + "?geocode" +adress
            + "&apikey=" + BuildConfig.GEOKOD_API_KEY
            )

        } catch (e: MalformedURLException) {
            Log.e(TAG, "Fail URI ", e)
            e.printStackTrace()

            listener.onFailed(e)
        }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }

}