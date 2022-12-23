package com.geekbrain.myapplication.detailes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.geekbrain.myapplication.R
import com.geekbrain.myapplication.model.Hours

class HourlyForeCastAdapter(): RecyclerView.Adapter<HourlyForeCastAdapter.HourViewHolder> () {

    private var dayForecast: List<Hours> = listOf()

    fun setHoursForecast(arrayWithHourForecast: List<Hours?>){
        dayForecast = arrayWithHourForecast?.let { it } as List<Hours>
        notifyDataSetChanged()
    }

    class HourViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val hour: TextView = view.findViewById(R.id.hour)
        val hourlyTemperatureTextView: TextView = view.findViewById(R.id.hourTemperatureValue)
        val hourlyPressureTextView: TextView = view.findViewById(R.id.hourPressureValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forecast_day_item, parent, false)
        return HourViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        holder.hour.text = dayForecast[position].hour
        holder.hourlyTemperatureTextView.text = dayForecast[position].temp.toString() + " C"
        holder.hourlyPressureTextView.text = dayForecast[position].precMm.toString() + " mm"

    }

    override fun getItemCount(): Int = dayForecast.size


}