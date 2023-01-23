package com.geekbrain.myapplication.repository

import android.annotation.SuppressLint
import android.content.*
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.geekbrain.myapplication.model.City
import com.geekbrain.myapplication.model.Weather
import com.geekbrain.myapplication.model.geoKod.CoordinatesDTO
import com.geekbrain.myapplication.model.weatherDTO.WeatherDTO
import com.geekbrain.myapplication.utils.Utils
import com.geekbrain.myapplication.viewmodel.CurrentPointState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationRepository private constructor(private val appContext: Context):
    SharedPreferences.OnSharedPreferenceChangeListener { //ApplicationContext

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


    private val TAG = LocationRepository::class.java.simpleName

    // CurrentLocation
    private var mLocation: Location? = null

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private var myReceiver = MyReceiver()

    // A reference to the service used to get location updates.
    private var mService: LocationUpdatesService? = null

    // Tracks the bound state of the service.
    private var mBound = false


    // Monitors the state of the connection to the service.
    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationUpdatesService.LocalBinder
            mService = binder.getService()
            mBound = true
            Log.i(TAG, "onServiceConnected: ")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mService = null
            mBound = false
        }
    }


    fun startLocationService() {
        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        appContext.bindService(
            Intent(appContext, LocationUpdatesService::class.java),
            mServiceConnection,
            Context.BIND_AUTO_CREATE
        )

        mService?.requestLocationUpdates(appContext)

        Log.i(TAG, "startLocationService: ")

        LocalBroadcastManager.getInstance(appContext)
            .registerReceiver(
                myReceiver, IntentFilter(
                    ACTION_BROADCAST
                )
            )
    }


    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private inner class MyReceiver : BroadcastReceiver() {

        @SuppressLint("SuspiciousIndentation")
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(context: Context?, intent: Intent?) {
            mLocation = intent?.getParcelableExtra(
                EXTRA_LOCATION,
            )
            Log.i(TAG, "onReceive: $mLocation")
            val locationLat = mLocation?.latitude?.toFloat()
            val locationLon = mLocation?.longitude?.toFloat()
            locationLat?.let { lat ->
                locationLon?.let { lon ->
                    weatherCurrentPointState = CurrentPointState.Success(
                        Weather(
                            City("Current Point", true, lat, lon),
                            null
                        )
                    )

                    remoteDataSource.getCityName(
                        lat, lon, callbackCityName
                    )
                }
            }

        }
    }

    private val callbackCityName = object : Callback<CoordinatesDTO> {
        override fun onResponse(call: Call<CoordinatesDTO>, response: Response<CoordinatesDTO>) {
            val geoKodResponse = response.body()
            Log.i(TAG, "onResponse: callbackCityName")
            if (Utils.checkResponseCoordinatesDTO(geoKodResponse)) {
                with(
                    response.body()?.response?.GeoObjectCollection?.featureMember?.get(0)?.
                        GeoObject
                ) {
                    val cityNameResponded = this?.name + ", " + this?.description
                    val isRusResponded = cityNameResponded.contains("Россия")
                    weatherCurrentPointState
                        .weatherInCurrentPoint.let {
                            it.city.city = cityNameResponded
                            it.city.isRus = isRusResponded

                            it.city.lon?.let { lon ->
                                it.city.lat?.let { lat ->
                                    remoteDataSource.getWeather(lat = lat, lon = lon,
                                        callbackWeatherDTO)
                                }
                            }
                        }
                }
            } else {
                throw RuntimeException("Cordinates are wrong")
            }

        }

        override fun onFailure(call: Call<CoordinatesDTO>, t: Throwable) {
            Log.i(TAG, "CoordinatesLoaderFailed: " + t.message)
            throw t
        }

    }

    val callbackWeatherDTO = object : Callback<WeatherDTO>{
        override fun onResponse(call: Call<WeatherDTO>, response: Response<WeatherDTO>) {
             response.body()?.let {
                if (Utils.checkResponseWeatherDTO(it)){
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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        TODO("Not yet implemented")
    }


}

