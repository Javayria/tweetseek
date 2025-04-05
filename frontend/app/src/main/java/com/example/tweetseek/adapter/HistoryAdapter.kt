package com.example.tweetseek.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tweetseek.R
import com.example.tweetseek.models.BirdReport

// Connects UI -> Data
class HistoryAdapter(private val reports: List<BirdReport>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    // Hold reference to components in layout
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val birdImage: ImageView = view.findViewById(R.id.birdImage)
        val birdName: TextView = view.findViewById(R.id.birdName)
    }

    // Create each "card" or dynamic component (view holder)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_history, parent, false)
        return ViewHolder(view)
    }

    // Bind data to each new view holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = reports[position]
        holder.birdName.text = report.birdName
        Glide.with(holder.itemView.context)
            .load(report.imageUrl)
            .placeholder(android.R.color.darker_gray)
            .into(holder.birdImage)
    }

    override fun getItemCount(): Int = reports.size
}
