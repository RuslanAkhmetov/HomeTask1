package com.geekbrain.myapplication.repository

import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import com.geekbrain.myapplication.BuildConfig
import com.geekbrain.myapplication.model.City
import com.geekbrain.myapplication.model.geoKod.CoordinatesDTO
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection
import kotlin.concurrent.thread

class CoordinatesLoader(
    private val listener: CoordinateLoaderListener,
    private val city: City,
) {
    private val TAG = "CoordinatesLoader"

    interface CoordinateLoaderListener {
        fun onLoaded(city: City)
        fun onFailed(throwable: Throwable)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getCoordinates() =
        try {
        val uri = URL(
            "https://geocode-maps.yandex.ru/1.x/" +
                    "?apikey=" + BuildConfig.GEOKOD_API_KEY +
                    "&geocode=" + city.city + "&format=json"
        )
        Log.i(TAG, "getCoordinates: " + uri.toString())
        val handler = Handler()
        lateinit var urlConnection: HttpsURLConnection
        try {

                urlConnection = (uri.openConnection() as HttpsURLConnection).apply {
                    requestMethod = "GET"
                    readTimeout = 10000
                }
            Thread {
                val bufferedReader: BufferedReader

                if (urlConnection.responseCode != HttpsURLConnection.HTTP_OK) {
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
                    handler.post {
                        listener.onLoaded(city)
                    }
                }
            }.start()
        } catch (e: Exception) {
            Log.e(TAG, "Fail connection ", e)
            e.printStackTrace()

            listener.onFailed(e)
        } finally {
            urlConnection.disconnect()
        }
    } catch (e: MalformedURLException) {
        Log.e(TAG, "Fail URI ", e)
        e.printStackTrace()

        listener.onFailed(e)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getCityName(){
        try {val uri = URL(
            "https://geocode-maps.yandex.ru/1.x/" +
                    "?apikey=" + BuildConfig.GEOKOD_API_KEY +
                    "&geocode=" + city.lon + "," + city.lat +
                    "&format=json"
        )
            Log.i(TAG, "getCoordinates: " + uri.toString())
            var handler = Handler()
            lateinit var urlConnection: HttpsURLConnection
            try {


                    urlConnection = (uri.openConnection() as HttpsURLConnection).apply {
                        requestMethod = "GET"
                        readTimeout = 10000
                    }
                Thread {
                    val bufferedReader: BufferedReader

                    if (urlConnection.responseCode != HttpsURLConnection.HTTP_OK) {
                        throw RuntimeException("Can't connect to ${uri.toString()}")
                    } else {
                        bufferedReader =
                            BufferedReader(InputStreamReader(urlConnection.inputStream))
                        val geoKodResponse: CoordinatesDTO =
                            Gson().fromJson(getLines(bufferedReader), CoordinatesDTO::class.java)

                        city.city =
                            geoKodResponse.response?.GeoObjectCollection?.featureMember?.get(0)?.GeoObject?.name

                        handler.post {
                            city?.let { listener.onLoaded(it) }
                        }
                        Log.i(TAG, "getCoordinates: ${city.city}")
                    }
                }.start()
            } catch (e: Exception) {
                Log.e(TAG, "Fail connection ", e)
                e.printStackTrace()

                listener.onFailed(e)
            } finally {
                urlConnection.disconnect()
            }

        } catch (e: MalformedURLException){
            Log.e(TAG, "Fail URI ", e)
            e.printStackTrace()
            listener.onFailed(e)
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }

}