package com.geekbrain.myapplication.repository

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.geekbrain.myapplication.WeatherApplication
import com.geekbrain.myapplication.model.*
import com.geekbrain.myapplication.model.geoKod.CoordinatesDTO
import com.geekbrain.myapplication.model.weatherDTO.WeatherDTO
import com.geekbrain.myapplication.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WeatherRepositoryImpl private constructor(private val  appContext: Context)
    : WeatherRepository { //context Application

    private val TAG = "WeatherRepository"

    private val remoteDataSource = RemoteDataSource()

    var listWeather: MutableList<Weather> = mutableListOf()

    private val localRepository: LocalRepository
            = LocalRepositoryImpl(WeatherApplication.getCityDao())

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

    override fun getWeatherFromRepository(): MutableList<Weather> {
        return listWeather
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun refreshWeatherList() {
        try {
            listWeather = getWeatherFromLocalStorage()
                        as MutableList<Weather>
            getWeatherListFromServer(listWeather)

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
            if (Utils.checkResponseCoordinatesDTO(response.body())) {
                val cityResponded = City(null, null, 0f, 0f)
                Log.i(TAG, "callbackCoordinatesDTO : onResponse: ")
                with(
                    response.body()?.response?.GeoObjectCollection?.featureMember?.get(0)
                        ?.GeoObject
                ) {

                    cityResponded.city = this?.name + ", " + this?.description
                    cityResponded.isRus = cityResponded.city?.contains("Россия")
                    val fl = this?.Point?.pos?.split(" ")?.map { it.toFloat() }
                    if (fl != null && fl.size >= 2) {
                        cityResponded.lon = fl.component1()
                        cityResponded.lat = fl.component2()
                    }
                    cityResponded.city?.let{ cityMame ->
                        listWeather.find { cityMame.contains(it.city.city as CharSequence)}
                            ?.city = cityResponded
                    }
                    Log.i(TAG, "callbackCoordinatesDTO: onResponse: ${listWeather.size}")
                    localRepository.saveEntity(cityResponded)
                    cityResponded.lat?.let { lat ->
                        cityResponded.lon?.let { lon ->
                            remoteDataSource.getWeather(lat, lon, callbackWeatherDTO)
                        }
                    }

                }
            }

        }


        override fun onFailure(call: Call<CoordinatesDTO>, t: Throwable) {
            Log.i(TAG, "callbackCoordinatesDTO : onFailure: ")
            t.printStackTrace()
            throw t
        }

    }

    private val callbackWeatherDTO = object : Callback<WeatherDTO> {
        override fun onResponse(call: Call<WeatherDTO>, response: Response<WeatherDTO>) {
            response.body()?.let { weatherDTOResponded ->
                if (Utils.checkResponseWeatherDTO(weatherDTOResponded)) {
                    weatherDTOResponded.info.lat?.let { lat ->
                        weatherDTOResponded.info.lon?.let { lon ->
                            listWeather.find { item ->
                                item.city.lat == lat && item.city.lon == lon }
                                ?.weatherDTO = response.body()

                        }
                    }
                }
            }
        }

        override fun onFailure(call: Call<WeatherDTO>, t: Throwable) {
            Log.i(TAG, "callbackWeatherDTO. onFailure: ")
            t.printStackTrace()
            throw t
        }

    }

    private fun getWeatherFromRemoteSource(lat: Float, lon: Float, callback: Callback<WeatherDTO>) {
        Log.i(TAG, "getWeatherFromServer: lat = $lat, lon = $lon ")
        remoteDataSource.getWeather(lat, lon, callback)
    }

    private fun getCityCoordinates(city: City, callback: Callback<CoordinatesDTO>) {
        Log.i(TAG, "getCityCoordinates: ")
        remoteDataSource.getCoordinates(city, callback)
    }

    override fun getWeatherFromLocalStorage(): List<Weather> {
        val cities = localRepository.getAllCities()
        return if (cities.isNotEmpty()){
            Log.i(TAG, "getWeatherFromLocalStorage: from db")
            cities.map { Weather(it, null) }
        } else{
            Log.i(TAG, "getWeatherFromLocalStorage: initialize")
            initWeatherList()
        }
    }


}