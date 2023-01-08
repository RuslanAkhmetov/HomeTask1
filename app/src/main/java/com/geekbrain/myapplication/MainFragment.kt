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
import com.geekbrain.myapplication.detailes.DetailsFragment
import com.geekbrain.myapplication.model.Weather
import com.geekbrain.myapplication.viewmodel.AppState
import com.geekbrain.myapplication.viewmodel.CurrentPointState
import com.geekbrain.myapplication.viewmodel.CurrentPointVieModel
import com.geekbrain.myapplication.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar


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
        _binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(): MainFragment = MainFragment()
    }

    private val viewModelCurrentPosition by viewModels<CurrentPointVieModel>()

    private val viewModel by viewModels<MainViewModel>()

    private var isDataSetRus: Boolean = true

    private val adapter = MainFragmentAdapter(object : MainFragmentAdapter.OnItemViewClickListener{
        override fun OnItemClick(weather: Weather) {
            activity?.supportFragmentManager?.apply {
                beginTransaction()
                    .add(R.id.container, DetailsFragment.newInstance(Bundle().apply {
                        putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
                    }))
                    .addToBackStack("")
                    .commitAllowingStateLoss()
            }
        }

    })

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentPointWeatherObserver = Observer<CurrentPointState>{
            fillCurrentPoint(it)
        }

        val observer = Observer<AppState> {
            renderData(it)
        }

        binding.mainFragmentRecyclerView.adapter = adapter
        binding.mainFragmentFAB.setOnClickListener{
            changeWeatherDataSet()
        }

        viewModel.getCurrentPointWeather()
            .observe(viewLifecycleOwner, currentPointWeatherObserver)

        viewModel.getLiveData().observe(viewLifecycleOwner, observer)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun fillCurrentPoint(currentPointState: CurrentPointState) {
        when(currentPointState){
            is CurrentPointState.Success -> {
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                binding.currentPoint
                    .mainFragmentRecyclerItemTextView
                    .text = "${currentPointState.weatherData.city.city}   " +
                        "${currentPointState.weatherData.weatherDTO?.fact?.temp.toString()}"
            }
            is CurrentPointState.Loading -> {
                binding.mainFragmentLoadingLayout.visibility = View.VISIBLE
            }
            is CurrentPointState.Error ->{
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                Log.i(TAG, "renderData: ${currentPointState.error.message}")
                binding.mainFragmentFAB.showSnackbar(
                    "Error"+currentPointState.error,
                    "Reload",
                    {viewModel.startMainViewModel()}
                )
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun renderData(appState: AppState){
        when(appState) {
            is AppState.Success -> {
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                adapter.setWeather(appState.weatherData.filter {
                        weather -> weather.city.isRus == isDataSetRus }
                    .toList())
            }

            is AppState.Loading -> {
                binding.mainFragmentLoadingLayout.visibility = View.VISIBLE
                Log.i(TAG, "renderData: loading")
            }

            is AppState.Error -> {
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                Log.i(TAG, "renderData: ${appState.error.message}")
                binding.mainFragmentFAB.showSnackbar(
                    "Error"+appState.error,
                    "Reload",
                    {viewModel.startMainViewModel()}
                )
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun changeWeatherDataSet(){
        if (isDataSetRus){
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        } else{
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        }.also {  isDataSetRus = !isDataSetRus }
        renderData(viewModel.getLiveData().value as AppState)
    }

    private fun View.showSnackbar(
        text: String,
        actionText: String,
        action: (View) -> Unit,
        length: Int = Snackbar.LENGTH_INDEFINITE
    ){
        Snackbar.make(this, text,length).setAction(actionText, action).show()
    }


}