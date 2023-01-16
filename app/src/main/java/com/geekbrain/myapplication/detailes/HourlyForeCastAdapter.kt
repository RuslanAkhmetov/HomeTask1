package com.geekbrain.myapplication.detailes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.geekbrain.myapplication.R
import com.geekbrain.myapplication.model.weatherDTO.Hours

private const val TAG ="HourlyForeCastAdapter"

class HourlyForeCastAdapter : RecyclerView.Adapter<HourlyForeCastAdapter.HourViewHolder> () {

    private var dayForecast: List<Hours> = listOf()

    fun setHoursForecast(arrayWithHourForecast: List<Hours?>){
        dayForecast = arrayWithHourForecast as List<Hours>
        notifyDataSetChanged()
    }

    class HourViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val hourTextView: TextView = view.findViewById(R.id.hour)
        private val hourlyTemperatureTextView: TextView = view.findViewById(R.id.hourTemperatureValue)
        private val hourlyPressureTextView: TextView = view.findViewById(R.id.hourPressureValue)
        private val hourlyHumidityTextView: TextView = view.findViewById(R.id.hourHumidityValue)
        private val weatherIcon: ImageView = view.findViewById(R.id.iconView)

        fun bind(hour: Hours){
            hourTextView.text = hour.hour
            hourlyTemperatureTextView.text =String.format("%d C", hour.temp)
            hourlyPressureTextView.text = String.format("%d mm",hour.pressureMm)
            hourlyHumidityTextView.text = String.format("%d %%", hour.humidity)

            weatherIcon.loadSvg(String.format("https://yastatic.net/weather/i/icons/funky/dark/%s.svg", hour.icon))

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

fun ImageView.loadSvg(url: String) {

    val imageLoader = ImageLoader.Builder(this.context)
        .componentRegistry { add(SvgDecoder(this@loadSvg.context)) }
        .build()

    val request = ImageRequest.Builder(this.context)
        .crossfade(true)
        .crossfade(500)
        //.placeholder(R.drawable.ic_baseline_error_outline_24)
        //.error(R.drawable.ic_baseline_error_outline_24)
        .data(url)
        .target(this)
        .build()

    imageLoader.enqueue(request)
}