package com.example.petapp.viewmodel.statistic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petapp.R
import com.example.petapp.data.model.StatisticTypeEntity

class TestAdapter(
    private var items: List<StatisticTypeEntity>
) : RecyclerView.Adapter<TestAdapter.StatisticViewHolder>() {

    inner class StatisticViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvUnit: TextView = itemView.findViewById(R.id.tvUnit)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.statistic_type_test, parent, false)
        return StatisticViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatisticViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvUnit.text = "Unit: ${item.unit}"
        holder.tvDescription.text = item.description ?: "No description"
    }

    override fun getItemCount(): Int = items.size

    fun setData(newItems: List<StatisticTypeEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}
