package com.geekbrain.myapplication.repository

import com.geekbrain.myapplication.model.geoKod.CoordinatesDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GetGeoKodAPI {
    @GET("1.x")
    fun getCoordinates(
        @Query("apikey") token : String,
        @Query("geocode") city: String,
        @Query("format") format: String
    ) : Call<CoordinatesDTO>

    @GET("1.x")
    fun getCityName(
        @Query("apikey") token : String,
        @Query("geocode") latAndLon: String,
        @Query("format") format: String
    ) : Call<CoordinatesDTO>

}