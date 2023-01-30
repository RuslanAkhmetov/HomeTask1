package com.geekbrain.myapplication.repository

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.geekbrain.myapplication.model.City
import com.geekbrain.myapplication.model.Weather
import com.geekbrain.myapplication.model.weatherDTO.WeatherDTO
import com.geekbrain.myapplication.utils.Utils
import com.geekbrain.myapplication.viewmodel.CurrentPointState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationRepository private constructor(private val appContext: Context) { //ApplicationContext
    private val TAG = LocationRepository::class.java.simpleName
    private val MIN_DISTANCE = 100f
    private val REFRESH_PERIOD = 100000L

    companion object {
        private var instance: LocationRepository? = null

        fun initialize(context: Context) {
            if (instance == null) {
                instance = LocationRepository(context)
            }
        }


        fun get() =
            instance ?: throw java.lang.RuntimeException("LocationRepository is not initialized")

    }


    var weatherCurrentPointStateLiveData: MutableLiveData<CurrentPointState> = MutableLiveData()

    private val remoteDataSource = RemoteDataSource()

    var weatherCurrentPointState = CurrentPointState
        .Success(
            Weather(
                City("Current Point", true, null, null),
                null
            )
        )


    // CurrentLocation
    private var mLocation: Location? = null


    // A reference to the service used to get location updates.
   // private var mService: LocationUpdatesService? = null

    // Tracks the bound state of the service.
    //private var mBound = false

    //Method 2 receive location

    var mAddress: MutableLiveData<String?> = MutableLiveData()


    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            getAddress(location)
        }

        override fun onFlushComplete(requestCode: Int) {
            super.onFlushComplete(requestCode)
        }

        override fun onProviderDisabled(provider: String) {
            super.onProviderDisabled(provider)
        }

        override fun onProviderEnabled(provider: String) {
            super.onProviderEnabled(provider)
        }

    }

    @RequiresApi(Build.VERSION_CODES.S)
    @Suppress("DEPRECATION")
    fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            val locationManager = appContext.getSystemService(Context.LOCATION_SERVICE)
                    as LocationManager
            /* if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                 val providerGPS = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                 providerGPS?.let {
                     locationManager.requestLocationUpdates(
                         LocationManager.GPS_PROVIDER,
                         REFRESH_PERIOD,
                         MIN_DISTANCE,
                         locationListener
                     )
                 }
             } else {*/
            val lastKnownLocation =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            lastKnownLocation?.let {
                getAddress(it)
            }
            /*}*/
        }
    }

    @Suppress("DEPRECATION")
    private fun getAddress(location: Location) {
        Log.i(TAG, "getAddress: location: $location")
        val handler = Handler()
        Thread {
            val geocoder = Geocoder(appContext)
            try {
                val listAddress = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude, 1
                )
                handler.post {
                    listAddress?.get(0)?.let {
                        Log.i(TAG, "getAddress: ${it.getAddressLine(0)}")
                        requestWeatherDTOForCurrentLocation(it.getAddressLine(0), location)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }.start()
    }

    private fun requestWeatherDTOForCurrentLocation(address: String, location: Location) {
        mLocation?.latitude = location.latitude
        mLocation?.longitude = location.longitude
       // mAddress.value = address
        Log.i(TAG, "setCoordinates: lat: ${location.latitude}")
        address?.let { addr ->
            location.latitude?.let { latitude ->
                location.longitude?.let { longitude ->
                    Log.i(TAG, "setCoordinates: address: $latitude $longitude")
                    weatherCurrentPointState = CurrentPointState.Success(
                        Weather(
                            City(addr, true, latitude.toFloat(), longitude.toFloat()),
                            null
                        )
                    )
                    Log.i(TAG, "setCoordinates: lat = $latitude")
                    remoteDataSource.getWeather(
                        latitude.toFloat(),
                        longitude.toFloat(), callbackWeatherDTO
                    )
                }
            }
        }
    }





    val callbackWeatherDTO = object : Callback<WeatherDTO> {
        override fun onResponse(call: Call<WeatherDTO>, response: Response<WeatherDTO>) {
            response.body()?.let {
                if (Utils.checkResponseWeatherDTO(it)) {
                    Log.i(TAG, "onResponse: callbackWeatherDTO")
                    weatherCurrentPointState
                        .weatherInCurrentPoint.weatherDTO = it
                    weatherCurrentPointStateLiveData.value = weatherCurrentPointState
                }
            }
        }

        override fun onFailure(call: Call<WeatherDTO>, t: Throwable) {
            Log.i(TAG, "weatherCallbackFailed: " + t.message)
            throw t
        }
    }

}

