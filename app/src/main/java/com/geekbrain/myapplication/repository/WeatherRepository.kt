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


class WeatherRepository private constructor(_context: Context) : Repository { //context Application

    private val TAG = "WeatherRepository"

    val context = _context

    private val remoteDataSource = RemoteDataSource()

    var listWeatherReceived: MutableList<Weather> =mutableListOf()

    override fun getWeatherFromRepository(): MutableList<Weather> = listWeatherReceived

    companion object {
        private var instance: WeatherRepository? = null
        fun initialize(context: Context) {
            if (instance == null)
                instance = WeatherRepository(context)
        }

        fun get(): WeatherRepository {
            return instance ?: throw IllegalStateException("MovieRepository must be initialized")
        }
    }

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
            Log.i(TAG, "getWeatherFromServer size of listweather: ${listWeather.size}")
            for (weatherItem in listWeather) {
                try {
                    if (weatherItem.city.lat == null || weatherItem.city.lon == null) {
                        getCityCoordinates(weatherItem.city, callbackCoordinatesDTO)
                    } else {
                        weatherItem.city.lat?.let { lat ->
                            weatherItem.city.lon?.let { lon ->
                                getWeatherFromRemoteSourse(lat, lon, callbackWeatherDTO)
                                Log.i(TAG, "getWeatherFromServer: ${weatherItem.city}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.i(TAG, "getWeatherFromServerFailed: " + e.message)
                    throw e
                }
            }
            Log.i(TAG, "getWeatherFromServer: listWeatherReceived " + listWeatherReceived.size)
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
                    /*WeatherLoader(onWeatherLoaderListener, city).apply {
                        loaderWeather()
                    }*/

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
            response.body()?.let {
                if (Utils.checkResponseWeatherDTO(response.body()!!)) {
                    val lat = response.body()!!.info.lat
                    val lon = response.body()!!.info.lon
                    Log.i(TAG, "onResponse: $lat, $lon")
                    lat?.let { lat ->
                        lon?.let { lon ->
                            listWeatherReceived.find { it.city.lat == lat && it.city.lon == lon }
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

    override fun getWeatherFromRemoteSourse(lat: Float, lon: Float, callback: Callback<WeatherDTO>) {
        Log.i(TAG, "getWeatherFromServer: ")
        remoteDataSource.getWeather(lat, lon, callback)
    }

    override fun getCityCoordinates(city: City, callback: Callback<CoordinatesDTO>) {
        Log.i(TAG, "getCityCoordinates: ")
        remoteDataSource.getCoordinates(city, callbackCoordinatesDTO)
    }

    override fun getWeatherFromLocalStorageRus(): List<Weather> = getRussianCities()

    override fun getWeatherFromLocalStorageWorld(): List<Weather> = getWorldCities()




}