package com.geekbrain.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.geekbrain.myapplication.databinding.FragmentRequestsLogBinding
import com.geekbrain.myapplication.model.RequestLog
import com.geekbrain.myapplication.viewmodel.LogViewModel
import kotlinx.android.synthetic.main.fragment_requests_log.*

class LogFragment: Fragment() {

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
        logViewModel.requestLogLiveData.observe(viewLifecycleOwner, {
            renderData(it)
        })
        logViewModel.makeRequestsLog()

        logViewModel.requestLogLiveData

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun renderData(requestsLog: MutableList<RequestLog>?) {
        binding.logFragmentRecyclerView.visibility = View.VISIBLE
        binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
        requestsLog?.let { adapter.setLogData(it) }

    }

}