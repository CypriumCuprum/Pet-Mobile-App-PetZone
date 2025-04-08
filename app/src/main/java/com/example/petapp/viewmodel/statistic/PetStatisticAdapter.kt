package com.example.petapp.viewmodel.statistic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.petapp.R
import com.example.petapp.data.model.PetStatisticEntity
import java.text.SimpleDateFormat
import java.util.*

class PetStatisticAdapter : ListAdapter<PetStatisticEntity, PetStatisticAdapter.PetStatisticViewHolder>(PetStatisticDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetStatisticViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_pet_health, parent, false)
        return PetStatisticViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetStatisticViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PetStatisticViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.statisticName)
        private val valueTextView: TextView = itemView.findViewById(R.id.statisticValue)
        private val dateTextView: TextView = itemView.findViewById(R.id.statisticDate)
        private val noteTextView: TextView = itemView.findViewById(R.id.statisticNote)

        fun bind(statistic: PetStatisticEntity) {
            nameTextView.text = "Statistic Type: ${statistic.statistic_typeid}"
            valueTextView.text = "Value: ${statistic.value}"
            dateTextView.text = statistic.recorded_at

            statistic.note?.let {
                noteTextView.text = it
                noteTextView.visibility = View.VISIBLE
            } ?: run {
                noteTextView.visibility = View.GONE
            }
        }

        private fun formatDate(date: Date): String {
            val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            return sdf.format(date)
        }
    }

    private class PetStatisticDiffCallback : DiffUtil.ItemCallback<PetStatisticEntity>() {
        override fun areItemsTheSame(oldItem: PetStatisticEntity, newItem: PetStatisticEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PetStatisticEntity, newItem: PetStatisticEntity): Boolean {
            return oldItem == newItem
        }
    }
}