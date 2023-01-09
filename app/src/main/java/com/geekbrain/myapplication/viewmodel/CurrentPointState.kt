package com.geekbrain.myapplication.viewmodel

import com.geekbrain.myapplication.model.Weather


sealed class CurrentPointState {
        data class Success(val weatherData: Weather): CurrentPointState()
        data class Error(val error: Throwable): CurrentPointState()
        object Loading: CurrentPointState()
}
