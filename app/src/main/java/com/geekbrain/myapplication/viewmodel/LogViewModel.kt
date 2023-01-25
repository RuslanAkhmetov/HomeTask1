package com.geekbrain.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.geekbrain.myapplication.model.RequestLog
import com.geekbrain.myapplication.repository.WeatherRepository
import com.geekbrain.myapplication.repository.WeatherRepositoryImpl

class LogViewModel(
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl.get(),
    var requestLogLiveData: MutableLiveData<MutableList<RequestLog>> =
        Transformations.switchMap(weatherRepository.requestLogLiveData)
        { MutableLiveData<MutableList<RequestLog>>(weatherRepository.getRequestsLog()) }
                as MutableLiveData<MutableList<RequestLog>>,

    //: MutableLiveData<MutableList<RequestLog>> = MutableLiveData(),

) : ViewModel() {

    private val TAG = "LogViewModel"

    fun getRequestsLog() {
        requestLogLiveData = weatherRepository.requestLogLiveData
    }


    fun makeRequestsLog() {
        Log.i(TAG, "makeRequestsLog: ")
        weatherRepository.makeRequestLog()
    }

    fun saveRequestToDB(requestLog: RequestLog) {
        weatherRepository.saveRequestToDB(requestLog)
    }

   /* requestLogLiveData  =
    Transformations.switchMap(weatherRepository.requestLogLiveData)
    { MutableLiveData<List<RequestLog>>(weatherRepository.getRequestsLog()) }*/
}

