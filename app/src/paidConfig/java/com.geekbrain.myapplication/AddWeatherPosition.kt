package com.geekbrain.myapplication

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.geekbrain.myapplication.databinding.FragmentWeatherPositionAddBinding
import com.geekbrain.myapplication.model.City
import com.geekbrain.myapplication.viewmodel.AddViewModel
import com.geekbrain.myapplication.viewmodel.CurrentPointState

class AddWeatherPosition: Fragment() {
    private val TAG = "addWeatherPosition"

    private val addPositionViewModel by viewModels<AddViewModel>()

    private var newPointWeatherState: CurrentPointState = CurrentPointState.Loading

    private var _binding : FragmentWeatherPositionAddBinding? = null
    private val binding get() = _binding!!

    companion object{
        @JvmStatic
        fun newInstance() = AddWeatherPosition()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherPositionAddBinding.inflate(inflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        addPositionViewModel.getCityNewWeatherLiveData()
            .observe(viewLifecycleOwner) {
                Log.i(TAG, "onViewCreated: ")
                if (it is CurrentPointState.Success) {
                    Log.i(TAG, "onViewCreated: Success")
                    newPointWeatherState = it
                    binding.foundName.text = it.weatherInCurrentPoint.city.city

                }
            }

        binding.searchNewPositionButton.setOnClickListener{
            val cityName = binding.newPositionEditText.text.toString()
            if(cityName.length > 3){
                addPositionViewModel.searchCity(cityName)
                addPositionViewModel.getCityNewWeatherLiveData()
            }
        }

        binding.AddButton.setOnClickListener{
            addPositionViewModel.addCityToDB()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}