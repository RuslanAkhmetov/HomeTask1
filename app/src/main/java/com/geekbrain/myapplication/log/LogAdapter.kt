package com.geekbrain.myapplication.log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geekbrain.myapplication.R
import com.geekbrain.myapplication.model.RequestLog
import kotlinx.android.synthetic.main.request_log_item.view.*
import java.util.*

class LogAdapter: RecyclerView.Adapter<LogAdapter.RecyclerItemViewHolder> () {

    inner class RecyclerItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(logData: RequestLog){
            if(layoutPosition != RecyclerView.NO_POSITION){
                itemView.recyclerViewItem.text =
                    String.format("%s %s  %.1f %s", logData.city, Date(logData.timestamp).toString(),
                        logData.temperature, logData.condition)
            }
        }

    }

    private var logData: List<RequestLog> = arrayListOf()

    fun setLogData(requestsLog: List<RequestLog>){
        this.logData = requestsLog
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerItemViewHolder {
        return RecyclerItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.request_log_item, parent, false) as View
        )
    }

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        holder.bind(logData[position])
    }

    override fun getItemCount(): Int {
        return logData.size
    }


}