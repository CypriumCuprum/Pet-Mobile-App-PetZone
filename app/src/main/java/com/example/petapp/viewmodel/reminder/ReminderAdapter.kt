package com.example.petapp.viewmodel.reminder

import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.example.petapp.data.model.Reminder
import com.example.petapp.databinding.ItemReminderBinding
import com.example.petapp.R
import com.example.petapp.data.model.ReminderEntity

class ReminderAdapter(
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    private var reminders = emptyList<ReminderEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(reminders[position])
    }

    override fun getItemCount(): Int {
        return reminders.size
    }

    fun submitList(newReminders: List<ReminderEntity>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = reminders.size
            override fun getNewListSize() = newReminders.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return reminders[oldItemPosition].id == newReminders[newItemPosition].id
            }
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return reminders[oldItemPosition] == newReminders[newItemPosition]
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        reminders = newReminders
        diffResult.dispatchUpdatesTo(this)
    }

    class ReminderViewHolder(
        val binding: ItemReminderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reminder: ReminderEntity) {
            binding.reminder = reminder
            println("Test:" + reminder.icon())
            binding.executePendingBindings()
        }
    }
}
