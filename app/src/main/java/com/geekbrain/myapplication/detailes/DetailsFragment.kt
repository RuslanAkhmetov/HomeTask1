package com.geekbrain.myapplication.detailes

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.geekbrain.myapplication.R
import com.geekbrain.myapplication.databinding.FragmentDetailsBinding
import com.geekbrain.myapplication.model.WeatherDTO
import com.geekbrain.myapplication.viewmodel.CoordinatesLoader
import com.geekbrain.myapplication.viewmodel.WeatherLoader


class DetailsFragment : Fragment() {
    private val TAG = "DetailsFragment"

    private var _binding: FragmentDetailsBinding? = null
    private val binding
        get() = _binding!!

    private val coordinatesLoaderListener =
        object : CoordinatesLoader.CoordinateLoaderListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onLoaded(pos: String) {

                val fl = pos.split(" ").map { it.toFloat() }
                    lon = fl[0]
                    lat = fl[1]
                val loader = WeatherLoader(onLoaderListener, lat, lon)
                loader.loaderWeather()
            }

            override fun onFailed(throwable: Throwable) {
                // execute error
            }

        }

    private val onLoaderListener: WeatherLoader.WeatherLoaderListener =
        object : WeatherLoader.WeatherLoaderListener {
            override fun onLoaded(weatherDTO: WeatherDTO) {
                displayWeather(weatherDTO)
            }

            override fun onFailed(throwable: Throwable) {
                // execute error
            }

        }

    private var city = "Москва"

    private var lat = 55.755825F
    private var lon = 37.6173f

    private var item = 0

    companion object {
        const val BUNDLE_EXTRA = "cityWeather"

        fun newInstance(bundle: Bundle): DetailsFragment {
            val fragment = DetailsFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    private val detailsFragmentAdapter = HourlyForeCastAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        city = arguments?.getString(BUNDLE_EXTRA) ?: "Москва"

        binding.mainView.visibility = View.GONE
        binding.loadingLayout.visibility = View.VISIBLE
        binding.HourlyForeCastRecyclerView.adapter = detailsFragmentAdapter

        val coordinatesLoader = CoordinatesLoader(coordinatesLoaderListener, city)
        coordinatesLoader.getCoordinates()
        Log.i(TAG, "onViewCreated: city : $city lat: $lat lon $lon")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun displayWeather(weatherDTO: WeatherDTO) {
        with(binding) {
            mainView.visibility = View.VISIBLE
            loadingLayout.visibility = View.GONE

            cityName.text = city
            cityCoordinates.text = String.format(
                getString(R.string.city_coordinates),
                lat.toString(),
                lon.toString(),
            )

            weatherCondition.text = weatherDTO.fact?.condition
            temperatureValue.text = weatherDTO.fact?.temp.toString()
            feelsLikeValue.text = weatherDTO.fact?.feelsLike.toString()
            windSpeedValue.text = weatherDTO.fact?.windSpeed.toString()
            windDirValue.text = weatherDTO.fact?.windDir
            pressureValue.text = weatherDTO.fact?.pressureMm.toString()
            humidityValue.text = weatherDTO.fact?.humidity.toString()

            forecastDateValue.text = weatherDTO.forecasts[item]?.date
            weatherDTO.forecasts[item]?.let { it1 ->
                detailsFragmentAdapter.setHoursForecast(
                    it1.hours
                )
            }

            Log.i(TAG, "displayWeather: ${dateLeft.isClickable}")
            dateLeft.setOnClickListener {
                if (item - 1 >= 0) {
                    item = --item % weatherDTO.forecasts.size
                    Log.i(TAG, "setOnClickListener: $item")
                    forecastDateValue.text = weatherDTO.forecasts[item]?.date
                    weatherDTO.forecasts[item]?.let { it1 ->
                        detailsFragmentAdapter.setHoursForecast(
                            it1.hours
                        )
                    }
                }
            }

            dateRight.setOnClickListener {
                item++
                item %= weatherDTO.forecasts.size
                Log.i(TAG, "setOnClickListener: $item")
                forecastDateValue.text = weatherDTO.forecasts[item]?.date
                weatherDTO.forecasts[item]?.let { it1 ->
                    detailsFragmentAdapter.setHoursForecast(
                        it1.hours
                    )
                }
            }

        }
    }


}