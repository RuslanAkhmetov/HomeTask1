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
import com.geekbrain.myapplication.WeatherApplication.Companion.MY_LOCATION_PERMISSION
import com.geekbrain.myapplication.WeatherApplication.Companion.sharedPreferences
import com.geekbrain.myapplication.databinding.FragmentMainBinding
import com.geekbrain.myapplication.detailes.DetailsFragment
import com.geekbrain.myapplication.model.Weather
import com.geekbrain.myapplication.viewmodel.AppState
import com.geekbrain.myapplication.viewmodel.CurrentPointState
import com.geekbrain.myapplication.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

private const val IS_RUS_KEY = "IS_RUS_KEY"


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    private val TAG = "mainFragment"

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34


    companion object {
        @JvmStatic
        fun newInstance(): MainFragment = MainFragment()

        class ConnectivityActionReceiver : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val noConnection =
                    intent?.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
                if (noConnection == true) {
                    Toast.makeText(context, "Connection Lost", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Connection Found", Toast.LENGTH_LONG).show()
                }

            }

        }
    }

    private val viewModel by viewModels<MainViewModel>()

    private var isDataSetRus: Boolean = true

    private fun setDataSet() {
        activity?.let {
            isDataSetRus = sharedPreferences
                .getBoolean(IS_RUS_KEY, false)
            Log.i(TAG, "setDataSet isDataSetRus: $isDataSetRus")
            if (isDataSetRus) {
                binding.mainFragmentFAB.setImageResource(R.drawable.ic_earth)
            } else {
                binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
            }
        }
    }

    private fun safePermissions(locationPermission: Boolean) {
            Log.i(TAG, "safePermissions: $locationPermission")

            sharedPreferences
                .edit()
                .putBoolean(MY_LOCATION_PERMISSION, locationPermission)
                .apply()
    }

    private fun safeDataSet() {
        sharedPreferences
                .edit()
                .putBoolean(IS_RUS_KEY, isDataSetRus)
                .apply()
    }


    private val connectivityActionReceiver = ConnectivityActionReceiver()


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        if (!checkPermission()) {
            safePermissions(false)
            requestPermissions()
        } else {
            safePermissions(true)
        }

        context?.let {
            @Suppress("DEPRECATION")
            registerReceiver(
                it,
                connectivityActionReceiver,
                IntentFilter(CONNECTIVITY_ACTION),
                ContextCompat.RECEIVER_EXPORTED
            )
        }
        return binding.root

    }

    private val adapter = MainFragmentAdapter(object : MainFragmentAdapter.OnItemViewClickListener {
        override fun onItemClick(weather: Weather) {
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

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentPointWeatherObserver = Observer<CurrentPointState> {
            fillCurrentPoint(it)
        }

        val listWeatherObserver = Observer<AppState> {
            renderData(it)
        }


        viewModel.getCurrentPointWeather()
            .observe(viewLifecycleOwner, currentPointWeatherObserver)

        viewModel.getWeatherListLiveData()
            .observe(viewLifecycleOwner, listWeatherObserver)


        binding.currentPoint.mainFragmentRecyclerItemTextView.visibility =
            if (!checkPermission()) {
                View.GONE
            } else {
                View.VISIBLE
            }

        setDataSet()

        binding.currentPoint.mainFragmentRecyclerItemTextView.setOnClickListener {
            if (viewModel.getCurrentPointWeather().value is CurrentPointState.Success) {
                val currentPointWeather =
                    (viewModel.getCurrentPointWeather().value as CurrentPointState.Success)
                        .weatherInCurrentPoint
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

        binding.addWeatherLocation.setOnClickListener{
            activity?.supportFragmentManager?.apply {
                beginTransaction()
                    .add(R.id.container, AddWeatherPosition.newInstance())
                    .addToBackStack("")
                    .commitAllowingStateLoss()
                viewModel.getWeatherListLiveData()

            }
        }

        binding.mainFragmentFAB.setOnClickListener {
            changeWeatherDataSet()
        }


    }

    override fun onStart() {
        super.onStart()
        sharedPreferences.registerOnSharedPreferenceChangeListener(viewModel)
    }

    override fun onStop() {
        super.onStop()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(viewModel)
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
                    currentPointState.weatherInCurrentPoint.city.city?.let {
                        "$it  ${
                            currentPointState.weatherInCurrentPoint.weatherDTO
                                ?.fact?.temp.toString()
                        } C"
                    }

            }
            is CurrentPointState.Loading -> {
                binding.mainFragmentLoadingLayout.visibility = View.VISIBLE
            }
            is CurrentPointState.Error -> {
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                Log.i(TAG, "renderData: ${currentPointState.error.message}")
                binding.mainFragmentFAB.showSnackBar(
                    "Error" + currentPointState.error,
                    "Reload",
                    { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        viewModel.startMainViewModel()
                    }
                    }
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                adapter.setWeather(appState.weatherList
                    .filter { weather -> weather.city.isRus == isDataSetRus }
                    .toList())
            }

            is AppState.Loading -> {
                binding.mainFragmentLoadingLayout.visibility = View.VISIBLE
                Log.i(TAG, "renderData: loading")
            }

            is AppState.Error -> {
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                Log.i(TAG, "renderData: ${appState.error.message}")
                binding.mainFragmentFAB.showSnackBar(
                    "Error" + appState.error,
                    "Reload",
                    { viewModel.startMainViewModel() }
                )
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun changeWeatherDataSet() {
        if (isDataSetRus) {
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        } else {
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        }.also { isDataSetRus = !isDataSetRus }
        safeDataSet()
        renderData(viewModel.getWeatherListLiveData().value as AppState)
    }

    private fun checkPermission() =
        context?.let {
            ContextCompat.checkSelfPermission(
                it, Manifest.permission.ACCESS_FINE_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED

    @Suppress("DEPRECATION")
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

    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(TAG, "onRequestPermissionsResult: $requestCode")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                safePermissions(true)
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                binding.currentPoint.mainFragmentRecyclerItemTextView.visibility = View.VISIBLE
            } else {
                safePermissions(false)
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                binding.currentPoint.mainFragmentRecyclerItemTextView.visibility = View.GONE
            }
        }
    }


    private fun View.showSnackBar(
        text: String,
        actionText: String,
        action: (View) -> Unit,
        length: Int = Snackbar.LENGTH_INDEFINITE
    ) {
        Snackbar.make(this, text, length).setAction(actionText, action).show()
    }

}