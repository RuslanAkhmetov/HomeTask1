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
import com.geekbrain.myapplication.utils.Utils
import com.geekbrain.myapplication.viewmodel.CurrentPointState

class LocationRepository private constructor(private val appContext: Context) :
    SharedPreferences.OnSharedPreferenceChangeListener {   //ApplicationContext
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

    interface LocationLoaderListener {
        fun onLoaded(location: Location)
    }

    var weatherCurrentPointStateLiveData: MutableLiveData<CurrentPointState> = MutableLiveData()

    var  weatherCurrentPointState = CurrentPointState
        .Success(
            Weather(
                City("Current Point", true, null, null),
                null))

   /* init {
        startLocationService()
    }*/

    fun getRepositoryContext(): Context = appContext


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
            mLocation = intent?.getParcelableExtra<Location>(
                EXTRA_LOCATION,
            )
            Log.i(TAG, "onReceive: $mLocation")

            val loaderCityName = CoordinatesLoader(
                    coordinatesLoaderListener,
                    City(
                        "Current Point",
                        true,
                        mLocation?.latitude?.toFloat(),
                        mLocation?.longitude?.toFloat()
                    )
                )
                loaderCityName.getCityName()

        }

        private val coordinatesLoaderListener =
            object : CoordinatesLoader.CoordinateLoaderListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onLoaded(city: City) {
                    WeatherLoader(onWeatherLoaderListener, city).apply {
                        loaderWeather()
                    }
                }

                override fun onFailed(throwable: Throwable) {
                    Log.i(TAG, "CoordinatesLoaderFailed: " + throwable.message)
                    throw throwable
                }

            }

        private val onWeatherLoaderListener: WeatherLoader.WeatherLoaderListener =
            object : WeatherLoader.WeatherLoaderListener {
                override fun onLoaded(weather: Weather) {
                    weatherCurrentPointState =
                        CurrentPointState.Success(weather)
                    weatherCurrentPointStateLiveData.value = weatherCurrentPointState
                }

                override fun onFailed(throwable: Throwable) {
                    Log.i(TAG, "weatherLoaderFailed: " + throwable.message)
                    throw throwable
                }

            }

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == Utils.KEY_REQUESTING_LOCATION_UPDATES) {

        }
    }


}

