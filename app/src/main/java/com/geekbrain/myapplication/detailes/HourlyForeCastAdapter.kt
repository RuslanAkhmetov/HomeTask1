package com.geekbrain.myapplication.detailes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.geekbrain.myapplication.R
import com.geekbrain.myapplication.model.Hours

class HourlyForeCastAdapter(): RecyclerView.Adapter<HourlyForeCastAdapter.HourViewHolder> () {

    private var dayForecast: List<Hours> = listOf()

    fun setHoursForecast(arrayWithHourForecast: List<Hours?>){
        dayForecast = arrayWithHourForecast?.let { it } as List<Hours>
        notifyDataSetChanged()
    }

    class HourViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val hourTextView: TextView = view.findViewById(R.id.hour)
        val hourlyTemperatureTextView: TextView = view.findViewById(R.id.hourTemperatureValue)
        val hourlyPressureTextView: TextView = view.findViewById(R.id.hourPressureValue)
        val hourlyHumidityTextView: TextView = view.findViewById(R.id.hourHumidityValue)
        val weatherIcon: ImageView = view.findViewById(R.id.iconView)

        fun bind(hour: Hours){
            hourTextView.text = hour.hour
            hourlyTemperatureTextView.text =String.format("%d C", hour.temp)
            hourlyPressureTextView.text = String.format("%d mm",hour.pressureMm)
            hourlyHumidityTextView.text = String.format("%d %%", hour.humidity)

            Glide.with(itemView)
                .load(String.format("https://yastatic.net/weather/i/icons/funky/dark/%s.svg.", hour.icon))
                    .into(weatherIcon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forecast_day_item, parent, false)
        return HourViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
            holder.bind(dayForecast[position])

    }

    override fun getItemCount(): Int = dayForecast.size


}