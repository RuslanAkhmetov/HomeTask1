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
import com.geekbrain.myapplication.model.Weather
import com.geekbrain.myapplication.model.WeatherDTO
import com.geekbrain.myapplication.viewmodel.WeatherLoader


class DetailsFragment : Fragment() {
    private val TAG = "DetailsFragment"

    private var _binding: FragmentDetailsBinding? = null
    private val binding
        get() = _binding!!

    private val onLoaderListener: WeatherLoader.WeatherLoaderListener =
        object : WeatherLoader.WeatherLoaderListener {
            override fun onLoaded(weatherDTO: WeatherDTO) {
                displayWeather(weatherDTO)
            }

            override fun onFailed(throwable: Throwable) {
                // execute error
            }

        }

    private lateinit var weatherBundle: Weather

    private var item = 0

    companion object {
        const val BUNDLE_EXTRA = "weather"

        fun newInstance(bundle: Bundle): DetailsFragment {
            val fragment = DetailsFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        weatherBundle = arguments?.getParcelable(BUNDLE_EXTRA) ?: Weather()

        binding.mainView.visibility = View.GONE
        binding.loadingLayout.visibility = View.VISIBLE

        val loader = WeatherLoader(onLoaderListener, weatherBundle.city.lat, weatherBundle.city.lon)
        loader.loaderWeather()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun displayWeather(weatherDTO: WeatherDTO) {
        with(binding) {
            mainView.visibility = View.VISIBLE
            loadingLayout.visibility = View.GONE

            val city = weatherBundle.city
            cityName.text = city?.city
            cityCoordinates.text = String.format(
                getString(R.string.city_coordinates),
                city?.lat.toString(),
                city?.lon.toString(),
            )

            weatherCondition.text = weatherDTO.fact?.condition
            temperatureValue.text = weatherDTO.fact?.temp.toString()
            feelsLikeValue.text = weatherDTO.fact?.feelsLike.toString()
            windSpeedValue.text = weatherDTO.fact?.windSpeed.toString()
            windDirValue.text = weatherDTO.fact?.windDir
            pressureValue.text = weatherDTO.fact?.pressureMm.toString()
            humidityValue.text = weatherDTO.fact?.humidity.toString()

            forecastDateValue.text = weatherDTO.forecasts[5]?.date

            Log.i(TAG, "displayWeather: ${dateLeft.isClickable}")
            dateLeft.setOnClickListener{
                item = item-- % weatherDTO.forecasts.size
                Log.i(TAG, "setOnClickListener: $item")
                forecastDateValue.text = weatherDTO.forecasts[item]?.date
            }

            dateRight.setOnClickListener{
                item++
                item %= weatherDTO.forecasts.size
                Log.i(TAG, "setOnClickListener: $item")
                forecastDateValue.text = weatherDTO.forecasts[item]?.date
            }

            /*minTempValue.text = weatherDTO.forecasts[1]?.parts?.day?.temp_min.toString()
            maxTempValue.text = weatherDTO.forecasts[1]?.parts?.day?.temp_max.toString()*/
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadWeather() {

    }


}