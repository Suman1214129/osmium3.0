package com.osmiumai.app.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.osmiumai.app.R

class TopicsPagerAdapter : RecyclerView.Adapter<TopicsPagerAdapter.PageViewHolder>() {

    class PageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerTopic: TextView = view.findViewById(R.id.headerTopic)
        val listContainer: LinearLayout = view.findViewById(R.id.listContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.page_topics, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val isWeak = position == 0
        holder.headerTopic.text = if (isWeak) "Weak Topics" else "Strong Topics"
        
        holder.listContainer.removeAllViews()
        val topics = if (isWeak) {
            listOf(
                "Electrostatic Potential & Capacitance" to "25.5%",
                "Gauss's Law and Applications" to "26.4%",
                "Photoelectric Effect" to "31.4%",
                "Moving Charges and Magnetism" to "36.6%",
                "Wave Optics - Young's Double Slit Experiment" to "45.9%"
            )
        } else {
            listOf(
                "Current Electricity - Kirchhoff's Laws" to "91.1%",
                "Semiconductor Devices - PN Junction" to "86.4%",
                "Stoke's Law" to "81.2%",
                "Logic Gates & Digital Electronics" to "76.6%",
                "Ray Optics - Refraction through Lenses" to "65.6%"
            )
        }

        topics.forEach { (topic, accuracy) ->
            val row = LayoutInflater.from(holder.itemView.context).inflate(R.layout.item_topic_row, holder.listContainer, false)
            row.findViewById<TextView>(R.id.tvTopic).text = topic
            row.findViewById<TextView>(R.id.tvAccuracy).apply {
                text = accuracy
                setTextColor(if (isWeak) 0xFF9E511E.toInt() else 0xFF4CAF50.toInt())
            }
            holder.listContainer.addView(row)
        }
    }

    override fun getItemCount() = 2
}
