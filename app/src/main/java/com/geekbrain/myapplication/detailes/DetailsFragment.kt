package com.geekbrain.myapplication.detailes

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.geekbrain.myapplication.R
import com.geekbrain.myapplication.databinding.FragmentDetailsBinding
import com.geekbrain.myapplication.model.Weather


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

        val weatherNullable: Weather? = arguments?.getParcelable(BUNDLE_EXTRA)
        weatherNullable?.let { weather = it }

        binding.mainView.visibility = View.GONE
        binding.loadingLayout.visibility = View.VISIBLE
        binding.HourlyForeCastRecyclerView.adapter = detailsFragmentAdapter
        displayWeather(weather)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun displayWeather(weather: Weather) {
        with(binding) {
            mainView.visibility = View.VISIBLE
            loadingLayout.visibility = View.GONE

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