package com.example.petapp.view.vaccinnationSchedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petapp.R
import com.example.petapp.viewmodel.VaccinationViewModel
import com.example.petapp.viewmodel.VaccinationViewModelFactory
import com.google.android.material.button.MaterialButton

class VaccinationScheduleFragment : Fragment() {

    private lateinit var viewModel: VaccinationViewModel
    private lateinit var adapter: VaccinationAdapter

    private lateinit var recyclerView: RecyclerView

    // Các nút filter
    private lateinit var btnAll: TextView
    private lateinit var btnCompleted: TextView
    private lateinit var btnUpcoming: TextView
    private lateinit var btnMissed: TextView

    private lateinit var addScheduleButton: MaterialButton

    // Danh sách các nút filter để dễ quản lý
    private lateinit var filterButtons: List<TextView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vaccination_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        initViews(view)

        // Setup ViewModel
        setupViewModel()

        // Setup RecyclerView
        setupRecyclerView()

        // Setup filter buttons
        setupFilterButtons()

        // Setup add button
        setupAddButton()

        // Observe live data
        observeData()
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.vaccinationRecyclerView)

        // Filter buttons
        btnAll = view.findViewById(R.id.btnAll)
        btnCompleted = view.findViewById(R.id.btnCompleted)
        btnUpcoming = view.findViewById(R.id.btnUpcoming)
        btnMissed = view.findViewById(R.id.btnMissed)

        // Tạo danh sách các nút filter
        filterButtons = listOf(btnAll, btnCompleted, btnUpcoming, btnMissed)

        addScheduleButton = view.findViewById(R.id.addScheduleButton)
    }

    private fun setupViewModel() {
        // Normally would use dependency injection or a ViewModelFactory
        // This is a simplified example
        val factory = VaccinationViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[VaccinationViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = VaccinationAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupFilterButtons() {
        // Default to "All" selected
        setSelectedFilter(btnAll)

        // Set click listeners for each filter button
        btnAll.setOnClickListener {
            setSelectedFilter(btnAll)
            viewModel.setFilterStatus(null)
        }

        btnCompleted.setOnClickListener {
            setSelectedFilter(btnCompleted)
            viewModel.setFilterStatus("Completed")
        }

        btnUpcoming.setOnClickListener {
            setSelectedFilter(btnUpcoming)
            viewModel.setFilterStatus("Upcoming")
        }

        btnMissed.setOnClickListener {
            setSelectedFilter(btnMissed)
            viewModel.setFilterStatus("Missed")
        }
    }

    // Thiết lập nút filter được chọn
    private fun setSelectedFilter(selectedButton: TextView) {
        // Đặt tất cả về trạng thái chưa chọn
        filterButtons.forEach { button ->
            button.setBackgroundResource(R.drawable.bg_filter_button_normal)
        }

        // Thiết lập nút được chọn
        selectedButton.setBackgroundResource(R.drawable.bg_filter_button_selected)
    }

    private fun setupAddButton() {
        addScheduleButton.setOnClickListener {
            // Trong ứng dụng thực tế, mở dialog hoặc điều hướng đến màn hình thêm mới
            showAddVaccinationDialog()
        }
    }

    private fun observeData() {
        viewModel.filteredVaccinations.observe(viewLifecycleOwner) { vaccinations ->
            adapter.submitList(vaccinations)
        }

        // Cho mục đích demo - tải dữ liệu mẫu nếu danh sách trống
        viewModel.allVaccinations.observe(viewLifecycleOwner) { vaccinations ->
            if (vaccinations.isEmpty()) {
                viewModel.addSampleData()
            }
        }
    }

    private fun showAddVaccinationDialog() {
        // Trong ứng dụng thực tế, bạn sẽ hiển thị dialog hoặc điều hướng đến màn hình khác
        // Ví dụ:
        AddVaccinationDialog().show(childFragmentManager, "add_vaccination")
    }
}