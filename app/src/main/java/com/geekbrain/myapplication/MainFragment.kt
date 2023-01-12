package com.geekbrain.myapplication

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.geekbrain.myapplication.databinding.FragmentMainBinding
import com.geekbrain.myapplication.detailes.DetailsFragment
import com.geekbrain.myapplication.model.Weather
import com.geekbrain.myapplication.viewmodel.AppState
import com.geekbrain.myapplication.viewmodel.CurrentPointState
import com.geekbrain.myapplication.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    private val TAG = "MainViewModel"

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34


    companion object {
        @JvmStatic
        fun newInstance(): MainFragment = MainFragment()
    }

    private val viewModel by viewModels<MainViewModel>()

    private var isDataSetRus: Boolean = true

    private val connectivityActionReceiver = ConnectivityActionReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        if (!checkPermission()) {
            requestPermissions()
        }

        context?.let {
            registerReceiver(
                it,
                connectivityActionReceiver,
                IntentFilter(CONNECTIVITY_ACTION),
                ContextCompat.RECEIVER_EXPORTED)
        }

            return binding.root

    }

    private val adapter = MainFragmentAdapter(object : MainFragmentAdapter.OnItemViewClickListener {
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

        val currentPointWeatherObserver = Observer<CurrentPointState> {
            fillCurrentPoint(it)
        }

        val listWeatherObserver = Observer<AppState> {
            renderData(it)
        }

        if (!checkPermission()) {
            binding.currentPoint.mainFragmentRecyclerItemTextView.visibility = View.GONE
        } else {
            binding.currentPoint.mainFragmentRecyclerItemTextView.visibility = View.VISIBLE
        }

        binding.currentPoint.mainFragmentRecyclerItemTextView.setOnClickListener {
            if (viewModel.getCurrentPointWeather().value is CurrentPointState.Success) {
                val currentPointWeather =
                    (viewModel.getCurrentPointWeather().value as CurrentPointState.Success)
                        .weatherCurrentPoint
                activity?.supportFragmentManager?.apply {
                    beginTransaction()
                        .add(R.id.container, DetailsFragment.newInstance(Bundle().apply {
                            putParcelable(DetailsFragment.BUNDLE_EXTRA, currentPointWeather)
                        }))
                        .addToBackStack("")
                        .commitAllowingStateLoss()
                }
            }
        }

        binding.mainFragmentRecyclerView.adapter = adapter

        binding.mainFragmentFAB.setOnClickListener {
            changeWeatherDataSet()
        }

        viewModel.getWeatherListLiveData()
            .observe(viewLifecycleOwner, listWeatherObserver)

        viewModel.getCurrentPointWeather()
            .observe(viewLifecycleOwner, currentPointWeatherObserver)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun fillCurrentPoint(currentPointState: CurrentPointState) {
        when (currentPointState) {
            is CurrentPointState.Success -> {
                binding.mainFragmentLoadingLayout.visibility = View.VISIBLE
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                binding.currentPoint
                    .mainFragmentRecyclerItemTextView
                    .text =
                    currentPointState.weatherCurrentPoint.city.city?.let {
                        String.format(
                            it, " ",
                            "currentPointState.weatherCurrentPoint.weatherDTO?.fact?.temp")
                    }
            }
            is CurrentPointState.Loading -> {
                binding.mainFragmentLoadingLayout.visibility = View.VISIBLE
            }
            is CurrentPointState.Error -> {
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                Log.i(TAG, "renderData: ${currentPointState.error.message}")
                binding.mainFragmentFAB.showSnackbar(
                    "Error" + currentPointState.error,
                    "Reload",
                    { viewModel.startMainViewModel() }
                )
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                adapter.setWeather(appState.weatherList.filter { weather -> weather.city.isRus == isDataSetRus }
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
                    "Error" + appState.error,
                    "Reload",
                    { viewModel.startMainViewModel() }
                )
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun changeWeatherDataSet() {
        if (isDataSetRus) {
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        } else {
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        }.also { isDataSetRus = !isDataSetRus }
        renderData(viewModel.getWeatherListLiveData().value as AppState)
    }

    private fun checkPermission() =
        context?.let {
            ContextCompat.checkSelfPermission(
                it, Manifest.permission.ACCESS_FINE_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermissions() {
        val shouldProvideRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )


        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")

            Snackbar.make(
                requireActivity().findViewById(R.id.container),
                R.string.permission_rationale,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(
                    R.string.ok,
                ) {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE,
                    )
                }.show()

        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            //ActivityCompat.
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE,
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(TAG, "onRequestPermissionsResult: $requestCode")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                binding.currentPoint.mainFragmentRecyclerItemTextView.visibility = View.VISIBLE
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                binding.currentPoint.mainFragmentRecyclerItemTextView.visibility = View.GONE
            }
        }
    }

    inner class ConnectivityActionReceiver: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val noConnection =
                intent?.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
            if(noConnection == true) {
                Toast.makeText(context, "Connection Lost", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Connection Found", Toast.LENGTH_LONG).show()
            }

        }

    }

    private fun View.showSnackbar(
        text: String,
        actionText: String,
        action: (View) -> Unit,
        length: Int = Snackbar.LENGTH_INDEFINITE
    ) {
        Snackbar.make(this, text, length).setAction(actionText, action).show()
    }

}