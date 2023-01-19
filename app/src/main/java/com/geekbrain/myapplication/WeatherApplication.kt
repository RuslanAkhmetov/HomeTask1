package com.geekbrain.myapplication

import android.app.Application
import androidx.room.Room
import com.geekbrain.myapplication.repository.LocationRepository
import com.geekbrain.myapplication.repository.WeatherRepositoryImpl
import com.geekbrain.myapplication.room.CityDao
import com.geekbrain.myapplication.room.CityDataBase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

class WeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appInstance = this
        WeatherRepositoryImpl.initialize(this)
        LocationRepository.initialize(this)
    }

    companion object {
        private var appInstance: WeatherApplication? = null
        private var db: CityDataBase? = null
        private const val DB_NAME = "Cities.db"

        @OptIn(InternalCoroutinesApi::class)
        fun getCityDao(): CityDao {
            if (db == null) {
                synchronized((CityDataBase::class.java)) {
                    if (db == null) {
                        if (appInstance == null) {
                            throw IllegalStateException(
                                "Application is null while creating Data Base")
                        }
                        db = Room.databaseBuilder(
                            appInstance!!.applicationContext,
                            CityDataBase::class.java,
                            DB_NAME
                        )
                            .allowMainThreadQueries()
                            .build()
                    }
                }
            }
            return db!!.citiDao()
        }

    }
}