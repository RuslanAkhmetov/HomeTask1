package com.geekbrain.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.geekbrain.myapplication.model.Weather

class MainFragmentAdapter (private var onItemViewClickListener: OnItemViewClickListener?) :
    RecyclerView.Adapter<MainFragmentAdapter.MainViewHolder>(){

    private var weatherData: List<Weather> = listOf()

    interface OnItemViewClickListener {
        fun OnItemClick(weather: Weather)
    }

    fun setWeather(data : List<Weather>){
        weatherData = data
        notifyDataSetChanged()
    }

    inner class MainViewHolder(view: View): RecyclerView.ViewHolder(view){
        fun bind(weather: Weather){
            itemView.apply {
                findViewById<TextView>(R.id.mainFragmentRecyclerItemTextView).text =
                    "${weather.city.city.toString()}     ${weather.weatherDTO?.fact?.temp.toString()}"

                setOnClickListener {
                    onItemViewClickListener?.OnItemClick(weather)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_main_recycler_item, parent, false) as View)

    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(weatherData[position])
    }

    override fun getItemCount(): Int {
        return weatherData.size
    }
}