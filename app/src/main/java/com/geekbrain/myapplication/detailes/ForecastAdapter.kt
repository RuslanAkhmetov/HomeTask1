package com.geekbrain.myapplication.detailes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geekbrain.myapplication.R
import com.geekbrain.myapplication.databinding.FragmentMainRecyclerItemBinding
import com.geekbrain.myapplication.model.Hours

class ForecastAdapter(private val hoursForecast: Array<Hours>):
    RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>(){


        class ForecastViewHolder(view: View): RecyclerView.ViewHolder(view){

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.forecast_day_item, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}