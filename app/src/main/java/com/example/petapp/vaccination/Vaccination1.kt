package com.example.petapp.vaccination

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petapp.R
import com.google.android.material.chip.Chip

class Vaccination1 : Fragment() {
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var rvVaccinationSchedule: RecyclerView? = null
    private var vaccinationList: MutableList<VaccinationModel>? = null
    private var adapter: VaccinationAdapter? = null
    private var chipAll: Chip? = null
    private var chipDone: Chip? = null
    private var chipUpcoming: Chip? = null
    private var chipCancelled: Chip? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vaccination1, container, false)

        // Initialize views
        rvVaccinationSchedule = view.findViewById<RecyclerView>(R.id.rvVaccinationSchedule)
        val btnCreateSchedule = view.findViewById<Button>(R.id.btnCreateSchedule)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val btnNotification = view.findViewById<ImageButton>(R.id.btnNotification)

        chipAll = view.findViewById<Chip>(R.id.chipAll)
        chipDone = view.findViewById<Chip>(R.id.chipDone)
        chipUpcoming = view.findViewById<Chip>(R.id.chipUpcoming)
        chipCancelled = view.findViewById<Chip>(R.id.chipCancelled)

        // Set up RecyclerView
        rvVaccinationSchedule.setLayoutManager(LinearLayoutManager(context))

        // Initialize data
        initVaccinationData()

        // Set up adapter
        adapter = VaccinationAdapter(context, vaccinationList)
        rvVaccinationSchedule.setAdapter(adapter)

        // Set up click listeners
        btnBack.setOnClickListener { v: View? ->
            if (activity != null) {
                activity!!.onBackPressed()
            }
        }

        btnNotification.setOnClickListener { v: View? -> }

        btnCreateSchedule.setOnClickListener { v: View? -> }

        // Set up chip group listeners
        setupChipListeners()

        return view
    }

    private fun setupChipListeners() {
        chipAll!!.setOnClickListener { v: View? -> filterVaccinations("all") }
        chipDone!!.setOnClickListener { v: View? -> filterVaccinations("Đã tiêm") }
        chipUpcoming!!.setOnClickListener { v: View? -> filterVaccinations("Sắp tới") }
        chipCancelled!!.setOnClickListener { v: View? -> filterVaccinations("Đã bỏ lỡ") }
    }

    private fun filterVaccinations(status: String) {
        val filteredList: MutableList<VaccinationModel> = ArrayList()

        if (status == "all") {
            adapter = VaccinationAdapter(context, vaccinationList)
        } else {
            for (vaccination in vaccinationList!!) {
                if (vaccination.status == status) {
                    filteredList.add(vaccination)
                }
            }
            adapter = VaccinationAdapter(context, filteredList)
        }

        rvVaccinationSchedule!!.adapter = adapter
    }

    private fun initVaccinationData() {
        vaccinationList = ArrayList()

        // Add sample data based on the screenshot
        vaccinationList.add(
            VaccinationModel(
                "ROUDY",
                "24/03/2025",
                "10:30",
                "Vắc-xin phòng bệnh dại",
                "Phòng khám thú y PetCare",
                "Sắp tới"
            )
        )

        vaccinationList.add(
            VaccinationModel(
                "ROUDY",
                "15/12/2024",
                "14:30",
                "Vắc-xin FHV-FCV (Viêm mũi khí quản, Calicivirus)",
                "Phòng khám thú y PetCare",
                "Đã tiêm"
            )
        )

        vaccinationList.add(
            VaccinationModel(
                "FURRY",
                "20/01/2024",
                "07:00",
                "Vắc-xin FPLV (Bệnh giảm bạch cầu)",
                "Phòng khám thú y PetCare",
                "Đã bỏ lỡ"
            )
        )

        vaccinationList.add(
            VaccinationModel(
                "BELLA",
                "05/08/2024",
                "15:30",
                "Vắc-xin FeLV (Bệnh bạch cầu ở mèo)",
                "Phòng khám thú y PetCare",
                "Đã tiêm"
            )
        )
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        fun newInstance(param1: String?, param2: String?): Vaccination1 {
            val fragment = Vaccination1()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}