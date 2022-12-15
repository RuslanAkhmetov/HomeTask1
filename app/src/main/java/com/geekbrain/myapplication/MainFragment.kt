package com.geekbrain.myapplication

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.geekbrain.myapplication.databinding.FragmentMainBinding
import com.geekbrain.myapplication.model.Weather
import com.geekbrain.myapplication.viewmodel.AppState
import com.geekbrain.myapplication.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar


/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    private val TAG = "MainViewModel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(): MainFragment = MainFragment()
    }

    private val viewModel by viewModels<MainViewModel>()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val observer = Observer<AppState> { renderData(it) }
        viewModel.getLiveData().observe(viewLifecycleOwner, observer)
        viewModel.getWeather()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun renderData(appState: AppState){
        when(appState) {
            is AppState.Success -> {
                val weatherData = appState.weatherData
                binding.loadingLayout.visibility = View.GONE
                setData(weatherData)
            }

            is AppState.Loading -> {
                binding.frameLayout.visibility = View.VISIBLE
                Log.i(TAG, "renderData: loading")
            }

            is AppState.Error -> {
                binding.loadingLayout.visibility = View.GONE
                Snackbar.make(binding.frameLayout, "Error", Snackbar.LENGTH_LONG)
                    .setAction("Reload") {viewModel.getWeather()}
                    .show()
            }
        }
    }

    private fun setData(weatherData: Weather) {
        binding.cityName.text = weatherData.city.city
        binding.cityCoordinates.text = String.format(
            getString(R.string.city_coordinates), weatherData.city.lat, weatherData.city.lon
        )
        binding.temperatureValue.text = weatherData.temperature.toString()
        binding.feelsLikeValue.text = weatherData.feelsLike.toString()
    }
}