package com.geekbrain.myapplication.log

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.geekbrain.myapplication.databinding.FragmentRequestsLogBinding
import com.geekbrain.myapplication.model.RequestLog
import com.geekbrain.myapplication.viewmodel.LogViewModel
import kotlinx.android.synthetic.main.fragment_requests_log.*

class LogFragment: Fragment() {
    
    private val TAG = "LogFragment"

    private var _binding : FragmentRequestsLogBinding? = null
    private val binding get() = _binding!!

    private val logViewModel by viewModels<LogViewModel>()

    private val adapter: LogAdapter by lazy { LogAdapter() }

    companion object{
        @JvmStatic
        fun newInstance() =
            LogFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRequestsLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logFragmentRecyclerView.adapter = adapter
        
        logViewModel.requestLogLiveData.observe(viewLifecycleOwner) {
            Log.i(TAG, "onViewCreated: observer: ${it.size}")
            renderData(it)
        }

        logViewModel.makeRequestsLog()

        logViewModel.getRequestsLog()

        Log.i(TAG, "onViewCreated:  logViewModel.requestLogLiveData: ${logViewModel.requestLogLiveData.value}")

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun renderData(requestsLog: MutableList<RequestLog>?) {
        binding.logFragmentRecyclerView.visibility = View.VISIBLE
        Log.i(TAG, "renderData: logsize = ${requestsLog?.size}")
        binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
        requestsLog?.let { adapter.setLogData(it) }

    }

}