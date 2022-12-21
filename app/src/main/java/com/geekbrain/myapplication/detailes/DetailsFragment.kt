package com.geekbrain.myapplication.detailes

import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

private const val Api_key = "2df68799-d465-4edc-8d15-d019d8f16728"

class DetailsFragment : Fragment(){
    private val TAG = "DetailsFragment"

    private var _binding: FragmentDetailsBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var weatherBundle: Weather

    companion object {
        const val BUNDLE_EXTRA = "weather"

        fun newInstance(bundle: Bundle): DetailsFragment{
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
        loadWeather()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun displayWeather(weatherDTO: WeatherDTO){
        with(binding){
            mainView.visibility = View.VISIBLE
            loadingLayout.visibility = View.GONE
            val city =  weatherBundle.city
            cityName.text =  weatherDTO.info.url.toString() //city?.city
            cityCoordinates.text = String.format(
                getString(R.string.city_coordinates),
                city?.lat.toString(),
                city?.lon.toString(),
            )
            weatherCondition.text = weatherDTO.fact?.condition
            temperatureValue.text =weatherDTO.fact?.temp.toString()
            feelsLikeValue.text = weatherDTO.fact?.feelsLike.toString()
            windSpeedValue.text = weatherDTO.fact?.windSpeed.toString()
            windDirValue.text = weatherDTO.fact?.windDir
            pressureValue.text = weatherDTO.fact?.pressureMm.toString()
            humidityValue.text = weatherDTO.fact?.humidity.toString()
            /*forecastDateValue.text = weatherDTO.forecasts[1]?.date

            minTempValue.text = weatherDTO.forecasts[1]?.parts?.day?.temp_min.toString()
            maxTempValue.text = weatherDTO.forecasts[1]?.parts?.day?.temp_max.toString()*/
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadWeather(){
        try{
            val uri =
                URL(("https://api.weather.yandex.ru/v2/forecast?lat=${weatherBundle.city?.lat}"
                        + "&lon=${weatherBundle.city?.lon}"
                        + "&lang=ru_RU"
                        + "&limit=5"))
                /*URL(("https://api.weather.yandex.ru/v2/informers?lat=${weatherBundle.city?.lat}&" +
                        "lon=${weatherBundle.city?.lon}"))*/
            val handler = Handler()
            Thread{
                lateinit var urlConnection: HttpsURLConnection
                try {
                    urlConnection=(uri.openConnection() as HttpsURLConnection).apply {
                        requestMethod = "GET"
                        addRequestProperty(
                            "X-Yandex-API-Key", Api_key
                        )
                        readTimeout = 10000
                    }
                    var inputStream: InputStream
                    val bufferedReader: BufferedReader
                    if (urlConnection.responseCode != HttpsURLConnection.HTTP_OK) {
                        inputStream = urlConnection.errorStream
                        Log.i(TAG, "loadWeather: " + urlConnection.responseCode)
                        throw RuntimeException("Can't connect to ${uri.toString()}")
                    } else{
                        bufferedReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                    }
                    val weatherDTO: WeatherDTO =
                        Gson().fromJson(getLines(bufferedReader), WeatherDTO::class.java)
                    handler.post{
                        displayWeather(weatherDTO)
                    }
                } catch (e: Exception){
                    Log.e(TAG, "Fail connection ", e)
                    e.printStackTrace()
                }
            }.start()
        } catch (e: MalformedURLException){
            Log.e(TAG, "Fail URL ", e )
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }
}