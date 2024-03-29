package com.geekbrain.myapplication.viewmodel

import com.geekbrain.myapplication.model.Weather

sealed class AppState{
    data class Success(val weatherList: List<Weather>): AppState()
    data class Error(val error: Throwable): AppState()
    object Loading: AppState()
}
