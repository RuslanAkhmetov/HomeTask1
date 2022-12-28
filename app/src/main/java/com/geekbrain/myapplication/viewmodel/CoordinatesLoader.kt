package com.geekbrain.myapplication.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.geekbrain.myapplication.BuildConfig
import com.geekbrain.myapplication.model.City
import com.geekbrain.myapplication.model.CoordinatesDTO
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

class CoordinatesLoader(
    val listener: CoordinateLoaderListener,
    val city: City,
) {
    private val TAG = "CoordinatesLoader"

    interface CoordinateLoaderListener {
        fun onLoaded(city: City)
        fun onFailed(throwable: Throwable)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getCoordinates() = try {
        val uri = URL(
            "https://geocode-maps.yandex.ru/1.x/" +
                    "?apikey=" + BuildConfig.GEOKOD_API_KEY +
                    "&geocode=" + city.city + "&format=json"
        )
        Log.i(TAG, "getCoordinates: " + uri.toString())
        //val handler = Handler()
        Thread {

            lateinit var urlConnection: HttpsURLConnection
            try {
                urlConnection = (uri.openConnection() as HttpsURLConnection).apply {
                    requestMethod = "GET"
                    readTimeout = 10000
                }

                val inputStream: InputStream
                val bufferedReader: BufferedReader

                if (urlConnection.responseCode != HttpsURLConnection.HTTP_OK) {
                    inputStream = urlConnection.errorStream
                    throw RuntimeException("Can't connect to ${uri.toString()}")
                } else {
                    bufferedReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                    val geoKodResponse: CoordinatesDTO =
                        Gson().fromJson(getLines(bufferedReader), CoordinatesDTO::class.java)

                    val pointPos =
                        geoKodResponse.response?.GeoObjectCollection?.featureMember?.get(0)?.GeoObject?.Point?.pos

                    Log.i(TAG, "getCoordinates: ${city.city} point = " + pointPos.toString())
                    val fl = pointPos?.split(" ")?.map { it.toFloat() }
                    if (fl != null) {
                        city.lon = fl[0]
                        city.lat = fl[1]
                    }

                    //handler.post {
                        city?.let { listener.onLoaded(it) }
                    //}
                }

            } catch (e: Exception) {
                Log.e(TAG, "Fail connection ", e)
                e.printStackTrace()

                //handler.post {
                    listener.onFailed(e)
                //}
            } finally {
                urlConnection.disconnect()
            }
        }.start()
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