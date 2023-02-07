package com.geekbrain.myapplication.repository

import android.os.Handler
import android.util.Log
import com.geekbrain.myapplication.model.City
import com.geekbrain.myapplication.model.RequestLog
import com.geekbrain.myapplication.room.CityDao
import com.geekbrain.myapplication.room.CityEntity
import com.geekbrain.myapplication.room.RequestEntity

private const val TAG = "localRepositoryImpl"

class LocalRepositoryImpl(private val localDaoSource: CityDao,
                          val  listener : DBLoadListener) : LocalRepository {

    interface DBLoadListener {
        fun onReceiveCitiesFromDB(cities: List<City>)
        fun onReceiveCitiesCount(count: Long)
        fun onReceiveRequestLog(requestLog: List<RequestLog>)
        fun onFailure()
    }

    override fun getAllCitiesAsync() {
        val handler = Handler()
        Thread{
            //localDaoSource.deleteLast(5)
            Log.i(TAG, "getAllCitiesAsync: ${localDaoSource.all().size}")
            val cities = convertCityEntityToCity(localDaoSource.all())
            handler.post{
                listener.onReceiveCitiesFromDB(cities)
            }
        }.start()
    }

    override fun getRequestLog() {
        val handler = Handler()
        Thread{
            val requests = localDaoSource.allRequest()
            handler.post{
                listener.onReceiveRequestLog(requests)
            }
        }.start()
    }

    override fun saveRequestToLog(requestLog: RequestLog) {
        Thread {
            val id = localDaoSource.getCityId(requestLog.city)
            Log.i(TAG, "saveRequestToLog: id= $id")
            localDaoSource.insertRequest(convertRequestLogToRequestEntity(requestLog, id))
        }.start()
    }



    override fun saveEntity(city: City) {
        Thread {
            localDaoSource.insert(convertCityToCityEntity(city))
            Log.i(TAG, "saveEntity: ++")
        }.start()
    }

    override fun deleteCities(finalId: Long) {
        Thread {
            localDaoSource.deleteLast(finalId)
        }.start()
    }

    override fun citiesCount() {
        val handler = Handler()

        Thread {
            val count = localDaoSource.cityEntityCount()
            handler.post {
                listener.onReceiveCitiesCount(count)
            }
        }. start()
    }



    private fun convertCityEntityToCity(entityList: List<CityEntity>): List<City> {
             return entityList.map {
                City(it.city, it.isRus, it.lat, it.lon)
            }
        }


    private fun convertCityToCityEntity(city: City): CityEntity {
        if (city.city != null && city.isRus != null && city.lat != null && city.lon != null) {
            return CityEntity(
                0,
                city.city!!,
                city.isRus!!,
                lat = city.lat!!,
                lon = city.lon!!
            )
        } else {
            throw java.lang.IllegalArgumentException("City.parameter  can't be null")
        }
    }

    private fun convertRequestLogToRequestEntity(requestLog: RequestLog, id: Long): RequestEntity {
        if(requestLog.city != null && requestLog.temperature != null && requestLog.timestamp != null
            && requestLog.condition!= null && id != null) {
            return RequestEntity(requestLog.timestamp,
                id,
                requestLog.temperature,
                requestLog.condition)
        } else{
            throw IllegalArgumentException("RequestLog or Id is null")
        }
    }

}
