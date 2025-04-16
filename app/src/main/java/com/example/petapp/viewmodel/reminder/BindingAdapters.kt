package com.example.petapp.viewmodel.reminder
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.petapp.data.model.ReminderEntity

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("reminderBackground")
    fun setReminderBackground(imageView: ImageView, reminder: ReminderEntity?) {
        reminder?.let {
            imageView.setBackgroundResource(reminder.icon())
        }
    }
}