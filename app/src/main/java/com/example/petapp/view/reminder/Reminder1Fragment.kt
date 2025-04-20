package com.example.petapp.view.reminder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petapp.data.model.ReminderEntity
import com.example.petapp.databinding.FragmentReminder1Binding
import com.example.petapp.viewmodel.reminder.ReminderAdapter
import com.example.petapp.viewmodel.reminder.RemindersViewModel

class Reminder1Fragment : Fragment() {
    private lateinit var viewModel: RemindersViewModel
    private lateinit var reminderAdapter: ReminderAdapter
    private var _binding: FragmentReminder1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminder1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(this)[RemindersViewModel::class.java]

        viewModel.allReminders.observe(viewLifecycleOwner) { existingReminders ->
            if (existingReminders.isNullOrEmpty()) {
                val testReminder = ReminderEntity(
                    type = "vet",
                    timeReminder = "2025-04-15 10:00:00",
                    repeat = "Everyday",
                    note = "Test reminder",
                    status = true
                )
                viewModel.insertReminder(testReminder)
            }
        }

        // Khởi tạo Adapter
        reminderAdapter = ReminderAdapter()

        // Cấu hình RecyclerView
        binding.recyclerViewReminders.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewReminders.adapter = reminderAdapter

        // Quan sát LiveData và cập nhật Adapter
        viewModel.allReminders.observe(viewLifecycleOwner) { newList ->
            reminderAdapter.submitList(newList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Giải phóng binding để tránh memory leak
    }
}
