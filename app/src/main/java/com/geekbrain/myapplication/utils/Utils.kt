package com.geekbrain.myapplication.utils

import android.content.Context
import android.location.Location
import android.preference.PreferenceManager
import com.geekbrain.myapplication.R
import com.geekbrain.myapplication.model.geoKod.CoordinatesDTO
import com.geekbrain.myapplication.model.weatherDTO.WeatherDTO
import java.text.DateFormat
import java.util.*

/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
internal object Utils {
    const val KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates"

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The [Context].
     */
    fun requestingLocationUpdates(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false)
    }

    /**
     * Stores the location updates state in SharedPreferences.
     * @param requestingLocationUpdates The location updates state.
     */
    fun setRequestingLocationUpdates(context: Context?, requestingLocationUpdates: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
            .apply()
    }

    /**
     * Returns the `location` object as a human readable string.
     * @param location  The [Location].
     */
    fun getLocationText(location: Location?): String {
        return if (location == null) "Unknown location" else "(" + location.latitude + ", " + location.longitude + ")"
    }

    fun getLocationTitle(context: Context): String {
        return context.getString(
            R.string.location_updated,
            DateFormat.getDateTimeInstance().format(Date())
        )
    }

    fun checkResponseCoordinatesDTO(geoKodResponse: CoordinatesDTO?): Boolean {
        return geoKodResponse?.response?.GeoObjectCollection?.featureMember?.get(0)?.GeoObject?.name != null &&
                geoKodResponse.response?.GeoObjectCollection?.featureMember?.get(0)?.GeoObject?.description != null &&
                geoKodResponse.response?.GeoObjectCollection?.featureMember?.get(0)?.GeoObject?.Point?.pos != null
    }

    fun checkResponseWeatherDTO(weatherDTO: WeatherDTO): Boolean {
        return (weatherDTO.fact?.temp != null && weatherDTO.geoObject?.district != null
                && weatherDTO.forecasts.isNotEmpty())
    }


}