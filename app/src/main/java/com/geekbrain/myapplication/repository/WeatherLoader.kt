package com.geekbrain.myapplication.repository

import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import com.geekbrain.myapplication.BuildConfig
import com.geekbrain.myapplication.model.City
import com.geekbrain.myapplication.model.Weather
import com.geekbrain.myapplication.model.WeatherDTO
import com.google.gson.Gson
import kotlinx.coroutines.handleCoroutineException
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

open class WeatherLoader(
    private val listener: WeatherLoaderListener,
    private val city: City?,
) {
    private val TAG = "WeatherLoader"

    interface WeatherLoaderListener {
        fun onLoaded(weather: Weather)
        fun onFailed(throwable: Throwable)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun loaderWeather() =

        try {
            val uri =
                URL(
                    "https://api.weather.yandex.ru/v2/forecast?lat=${city?.lat}"
                            + "&lon=${city?.lon}"
                            + "&lang=ru_RU"
                            + "&limit=4"
                )

            lateinit var urlConnection: HttpsURLConnection
            try {
                urlConnection = (uri.openConnection() as HttpsURLConnection).apply {
                    requestMethod = "GET"
                    addRequestProperty("X-Yandex-API-Key", BuildConfig.WEATHER_API_KEY)
                    readTimeout = 10000
                }
                val handler = Handler()
                Thread {
                    val inputStream: InputStream
                    val bufferedReader: BufferedReader
                    Log.i(TAG, "loaderWeather: $uri")
                    if (urlConnection.responseCode != HttpsURLConnection.HTTP_OK) {
                        inputStream = urlConnection.errorStream
                        Log.i(TAG, "loadWeather: " + urlConnection.responseCode + inputStream)
                        throw RuntimeException("Can't connect to ${uri}")
                    } else {
                        bufferedReader =
                            BufferedReader(InputStreamReader(urlConnection.inputStream))
                    }
                    val weatherDTO: WeatherDTO =
                        Gson().fromJson(getLines(bufferedReader), WeatherDTO::class.java)
                    val weather = city?.let { Weather(it, weatherDTO) }
                    if (weather != null) {
                        handler.post {
                            listener.onLoaded(weather)
                        }
                    } else {
                        throw RuntimeException("WeatherDTO is not loaded")
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
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }
}

