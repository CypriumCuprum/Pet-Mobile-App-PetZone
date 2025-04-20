package com.example.petapp.viewmodel.pet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.example.petapp.R

class CalendarAdapter(private val days: List<DayItem>) :
    RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        val tvMonth: TextView = itemView.findViewById(R.id.tvMonth)
        val tvWeekDay: TextView = itemView.findViewById(R.id.tvWeekDay)
        val tvEventCount: TextView = itemView.findViewById(R.id.tvEventCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_day_card, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val dayItem = days[position]
        holder.tvDay.text = dayItem.day
        holder.tvMonth.text = dayItem.month
        holder.tvWeekDay.text = dayItem.weekDay
        holder.tvEventCount.text = dayItem.eventCount

        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams


        if (position == 0 || position == 2) {
            layoutParams.setMargins(5, 0, 5, 20)
        }
        if (position == 4) {
            layoutParams.setMargins(5, 0, 5, 0)
        }
        if (position == 1 || position == 3) {
            layoutParams.setMargins(10, 0, 0, 20)
        }
        if (position == 5) {
            layoutParams.setMargins(10, 0, 0, 0)
        }
    }

    override fun getItemCount(): Int = days.size

}

data class DayItem(
    val day: String,
    val month: String,
    val weekDay: String,
    val eventCount: String
)
