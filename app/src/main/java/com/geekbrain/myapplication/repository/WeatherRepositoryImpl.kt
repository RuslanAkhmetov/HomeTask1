package com.geekbrain.myapplication.repository

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.geekbrain.myapplication.model.*
import com.geekbrain.myapplication.model.geoKod.CoordinatesDTO
import com.geekbrain.myapplication.model.weatherDTO.WeatherDTO
import com.geekbrain.myapplication.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WeatherRepositoryImpl private constructor(private val  appContext: Context) : WeatherRepository { //context Application

    private val TAG = "WeatherRepository"

    private val remoteDataSource = RemoteDataSource()

    var listWeatherReceived: MutableList<Weather> = mutableListOf()

    companion object {
        private var instance: WeatherRepositoryImpl? = null
        fun initialize(context: Context) {
            if (instance == null)
                instance = WeatherRepositoryImpl(context)
        }
        fun get(): WeatherRepositoryImpl {
            return instance ?: throw IllegalStateException("MovieRepository must be initialized")
        }

    }

    override fun getWeatherFromRepository(): MutableList<Weather> = listWeatherReceived

    @RequiresApi(Build.VERSION_CODES.N)
    override fun refreshWeatherList() {
        try {
            val listWeatherSent =
                (getWeatherFromLocalStorageRus() + getWeatherFromLocalStorageWorld())
                        as MutableList<Weather>
            getWeatherListFromServer(listWeatherSent)

        } catch (e: Exception) {
            throw e
        }
    }



    @RequiresApi(Build.VERSION_CODES.N)
    override fun getWeatherListFromServer(listWeather: List<Weather>) {
            for (weatherItem in listWeather) {
                try {
                    if (weatherItem.city.lat == null || weatherItem.city.lon == null) {
                        getCityCoordinates(weatherItem.city, callbackCoordinatesDTO)
                    } else {
                        weatherItem.city.lat?.let { lat ->
                            weatherItem.city.lon?.let { lon ->
                                getWeatherFromRemoteSource(lat, lon, callbackWeatherDTO)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.i(TAG, "getWeatherFromServerFailed: " + e.message)
                    throw e
                }
            }
    }

    private val callbackCoordinatesDTO = object : Callback<CoordinatesDTO> {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onResponse(call: Call<CoordinatesDTO>, response: Response<CoordinatesDTO>) {
            Log.i(TAG, "onResponse: ")
            if (Utils.checkResponseCoordinatesDTO(response.body())) {
                val city = City(null, null, 0f, 0f)
                with(
                    response.body()?.response?.GeoObjectCollection?.featureMember?.get(0)?.GeoObject
                ) {

                    city.city = this?.name + ", " + this?.description
                    city.isRus = city.city?.contains("Россия")
                    val fl = this?.Point?.pos?.split(" ")?.map { it.toFloat() }
                    if (fl != null && fl.size >= 2) {
                        city.lon = fl.component1()
                        city.lat = fl.component2()
                    }
                    listWeatherReceived.add(Weather(city, null))
                    city.lat?.let { lat ->
                        city.lon?.let { lon ->
                            remoteDataSource.getWeather(lat, lon, callbackWeatherDTO)
                        }
                    }

                }
            }

        }


        override fun onFailure(call: Call<CoordinatesDTO>, t: Throwable) {
            Log.i(TAG, "onFailure: ")
            throw t
        }

    }

    private val callbackWeatherDTO = object : Callback<WeatherDTO> {
        override fun onResponse(call: Call<WeatherDTO>, response: Response<WeatherDTO>) {
            Log.i(TAG, "onResponse: WeatherDTO")
            response.body()?.let { it ->
                if (Utils.checkResponseWeatherDTO(response.body()!!)) {
                    it.info.lat?.let { lat ->
                        it.info.lon?.let { lon ->
                            listWeatherReceived.find {it1 ->
                                it1.city.lat == lat && it1.city.lon == lon }
                                ?.weatherDTO = response.body()
                        }
                    }
                }
            }
        }

        override fun onFailure(call: Call<WeatherDTO>, t: Throwable) {
            Log.i(TAG, "onFailure: ")
            throw t
        }

    }

    private fun getWeatherFromRemoteSource(lat: Float, lon: Float, callback: Callback<WeatherDTO>) {
        Log.i(TAG, "getWeatherFromServer: ")
        remoteDataSource.getWeather(lat, lon, callback)
    }

    private fun getCityCoordinates(city: City, callback: Callback<CoordinatesDTO>) {
        Log.i(TAG, "getCityCoordinates: ")
        remoteDataSource.getCoordinates(city, callback)
    }

    override fun getWeatherFromLocalStorageRus(): List<Weather> = getRussianCities()

    override fun getWeatherFromLocalStorageWorld(): List<Weather> = getWorldCities()

}