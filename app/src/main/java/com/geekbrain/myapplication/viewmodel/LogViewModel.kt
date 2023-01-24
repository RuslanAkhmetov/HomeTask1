package com.geekbrain.myapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geekbrain.myapplication.model.RequestLog
import com.geekbrain.myapplication.repository.WeatherRepository
import com.geekbrain.myapplication.repository.WeatherRepositoryImpl

class LogViewModel(
    var requestLogLiveData: MutableLiveData<MutableList<RequestLog>> = MutableLiveData(),
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl.get(),
) : ViewModel() {

    private val TAG = "LogViewModel"

    fun getRequestsLog() {
        requestLogLiveData = weatherRepository.requestLogLiveData
    }


    fun makeRequestsLog() {
        weatherRepository.makeRequestLog()
    }

    fun saveRequestToDB(requestLog: RequestLog) {
        weatherRepository.saveRequestToDB(requestLog)
    }

/*    private var requestLogLiveData   =
        Transformations.switchMap(weatherRepository.requestLogLiveData)
        {MutableLiveData<List<RequestLog>>(weatherRepository.getRequestsLog())}*/
}

