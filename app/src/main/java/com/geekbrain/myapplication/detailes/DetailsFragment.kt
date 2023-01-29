package com.geekbrain.myapplication.detailes

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.geekbrain.myapplication.R
import com.geekbrain.myapplication.databinding.FragmentDetailsBinding
import com.geekbrain.myapplication.model.RequestLog
import com.geekbrain.myapplication.model.Weather
import com.geekbrain.myapplication.viewmodel.LogViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import java.util.*


class DetailsFragment : Fragment() {
    private val TAG = "DetailsFragment"

    private var _binding: FragmentDetailsBinding? = null
    private val binding
        get() = _binding!!


    private lateinit var weather: Weather

    private var item = 0

    companion object {
        const val BUNDLE_EXTRA = "CityWeather"

        fun newInstance(bundle: Bundle): DetailsFragment {
            val fragment = DetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val logViewModel by viewModels<LogViewModel>()

    private val detailsFragmentAdapter = HourlyForeCastAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        try {
            MapKitFactory.initialize(activity)
        } catch (e: Exception){
            Log.i(TAG, "onCreateView: Error ")
            e.printStackTrace()
        }
        return binding.root
    }

    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val weatherNullable: Weather? = arguments?.getParcelable(BUNDLE_EXTRA)
        weatherNullable?.let { weather = it }

        if (weather.city.city != null && weather.weatherDTO?.fact?.temp != null
            && weather.weatherDTO?.fact?.condition != null
        ) {
            val request = RequestLog(
                city = weather.city.city!!,
                timestamp = Date().time,
                temperature = weather.weatherDTO!!.fact?.temp?.toFloat()!!,
                condition = weather.weatherDTO!!.fact?.condition!!
            )
            logViewModel.saveRequestToDB(request)
        }

        binding.mainView.visibility = View.GONE
        //binding.loadingLayout.visibility = View.VISIBLE
        binding.HourlyForeCastRecyclerView.adapter = detailsFragmentAdapter
        if (weather.city.lat != null && weather.city.lon != null) {
            Log.i(TAG, "onViewCreated: mapview")
            binding.mapview.map.move(
                CameraPosition(
                    Point(
                        weather.city.lat!!.toDouble(),
                        weather.city.lon!!.toDouble()
                    ), 11.0f, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 0.0f),
                null
            )
        }
        displayWeather(weather)

    }

    override fun onStart() {
        binding.mapview.onStart()
        MapKitFactory.getInstance().onStart()
        super.onStart()
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun displayWeather(weather: Weather) {
        with(binding) {
            mainView.visibility = View.VISIBLE
            //loadingLayout.visibility = View.GONE

            cityName.text = weather.city.city
            cityCoordinates.text = String.format(
                getString(R.string.city_coordinates),
                weather.city.lat.toString(),
                weather.city.lon.toString(),
            )

            weatherCondition.text = weather.weatherDTO?.fact?.condition
            temperatureValue.text = weather.weatherDTO?.fact?.temp.toString()
            feelsLikeValue.text = weather.weatherDTO?.fact?.feelsLike.toString()
            windSpeedValue.text = weather.weatherDTO?.fact?.windSpeed.toString()
            windDirValue.text = weather.weatherDTO?.fact?.windDir
            pressureValue.text = weather.weatherDTO?.fact?.pressureMm.toString()
            humidityValue.text = weather.weatherDTO?.fact?.humidity.toString()

            forecastDateValue.text = weather.weatherDTO?.forecasts?.get(item)?.date
            weather.weatherDTO?.forecasts?.get(item)?.let { it1 ->
                detailsFragmentAdapter.setHoursForecast(
                    it1.hours
                )

            }

            dateLeft.setOnClickListener {
                var listSize = 0
                weather.weatherDTO?.forecasts?.size?.let { listSize = it }
                if (item - 1 >= 0) {
                    if (listSize != 0) {
                        item = --item % listSize
                        forecastDateValue.text =
                            weather.weatherDTO?.forecasts?.get(item)?.date
                        weather.weatherDTO?.forecasts?.get(item)?.let { it1 ->
                            detailsFragmentAdapter.setHoursForecast(
                                it1.hours
                            )
                        }
                    }
                }

            }

            dateRight.setOnClickListener {
                var listSize: Int
                weather.weatherDTO?.forecasts?.size?.let { listSize = it }
                listSize = weather.weatherDTO!!.forecasts.size
                if (listSize != 0) {
                    item = ++item % listSize
                    forecastDateValue.text = weather.weatherDTO?.forecasts?.get(item)?.date
                    weather.weatherDTO?.forecasts?.get(item)?.let { it1 ->
                        detailsFragmentAdapter.setHoursForecast(
                            it1.hours
                        )
                    }
                }
            }

        }
    }

}