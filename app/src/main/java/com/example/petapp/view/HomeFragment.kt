package com.example.petapp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.petapp.R
import com.example.petapp.data.local.AppDatabase
import com.example.petapp.data.repository.UserRepository
import com.example.petapp.view.auth.LoginActivity
import com.example.petapp.view.pet.AddPetActivity
import com.example.petapp.viewmodel.pet.CalendarAdapter
import com.example.petapp.viewmodel.pet.DayItem
import com.example.petapp.viewmodel.pet.PetViewModel
import com.example.petapp.viewmodel.user.LoginViewModel
import com.example.petapp.view.medical_report.MedicalReportListFragment
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var recyclerViewHorizontalYourPet: RecyclerView
    private lateinit var buttonAddPet: Button
    private lateinit var petViewModel: PetViewModel
    private lateinit var yourpetAdapter: YourPetAdapter
    private lateinit var recyclerViewCalendar: RecyclerView
    private lateinit var btnFoodNutrition: LinearLayout
    private lateinit var btnMedicalReport: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        petViewModel = ViewModelProvider(
            this,
            PetViewModel.Factory(requireActivity().application)
        )[PetViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        recyclerViewHorizontalYourPet = view.findViewById(R.id.recyclerViewHorizontal)
        //debug log
        println("Initializing RecyclerView: $recyclerViewHorizontalYourPet")
        buttonAddPet = view.findViewById(R.id.buttonAddPet)
        recyclerViewCalendar = view.findViewById<RecyclerView>(R.id.rvDays)
        btnFoodNutrition = view.findViewById(R.id.btn_food_nutrition)
        btnMedicalReport = view.findViewById(R.id.btn_medical_report)

        setupYourPetRecyclerView()
        setupCalenderView()
        loadPets()

        // Set up add pet button click listener
        buttonAddPet.setOnClickListener {
            navigateToAddPet()
        }

        clickMedicalReport()

    }

    @SuppressLint("CommitTransaction", "SetTextI18n")
    private fun clickMedicalReport() {
        btnMedicalReport.setOnClickListener {
            // navigate to medical report MedicalReportListFragment
            val medicalReportListFragment = MedicalReportListFragment()
            val toolbarview =
                requireActivity().findViewById<View>(R.id.toolbar)
            val toolbarTextView = toolbarview.findViewById<TextView>(R.id.toolbar_title)
            toolbarTextView.text = "Medical Report"
            toolbarview.visibility = View.VISIBLE

            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, medicalReportListFragment)
            transaction.addToBackStack(null) // Add to back stack if you want to allow back navigation
            transaction.commit()
        }
    }


    private fun setupCalenderView() {
        val days = listOf(
            DayItem("25", "February", "Tu", "+3"),
            DayItem("26", "February", "Wed", "+3"),
            DayItem("27", "February", "Th", "+3"),
            DayItem("28", "February", "Fr", "+3"),
            DayItem("01", "February", "Sa", "+3"),
            DayItem("02", "February", "Su", "+3")
        )

        val adapter = CalendarAdapter(days)

        recyclerViewCalendar.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerViewCalendar.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        // Additional check for returning to the fragment
        // This will catch cases where the activity result launcher might not work
        loadPets()
    }

    private fun navigateToAddPet() {
        val intent = Intent(requireContext(), AddPetActivity::class.java)
        startActivity(intent)
    }

    private fun setupYourPetRecyclerView() {
        yourpetAdapter = YourPetAdapter()
        //debug log
        println("Setting up RecyclerView with adapter: $yourpetAdapter")
        recyclerViewHorizontalYourPet.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = yourpetAdapter
        }
        println("RecyclerView setup complete with layout manager: ${recyclerViewHorizontalYourPet.layoutManager}")
    }

    private fun loadPets() {
        val loginViewModel = ViewModelProvider(
            requireActivity(),
            LoginViewModel.Factory(
                requireActivity().application
            )
        )[LoginViewModel::class.java]

        // Get the current user ID from the LoginViewModel
        val userId = loginViewModel.getLoggedInUserId()
//        val hardCodePet = listOf(
//            PetReduceForHome("1", "Pet 1", ""),
//            PetReduceForHome("2", "Pet 2", ""),
//            PetReduceForHome("3", "Pet 3", ""),
//            PetReduceForHome("4", "Pet 4", ""),
//            PetReduceForHome("5", "Pet 5", ""),
//            PetReduceForHome("6", "Pet 6", ""),
//            PetReduceForHome("7", "Pet 7", ""),
//            PetReduceForHome("8", "Pet 8", ""),
//        )
//        petAdapter.submitList(hardCodePet)

        if (userId != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                val pets = petViewModel.getPetsForHome(userId)
                println("Pets for user $userId: $pets") // Debug log
                yourpetAdapter.submitList(pets)
            }
        } else {
            // Handle case where user ID is null (e.g., show a message or redirect to login)
            redirectToLogin()
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun redirectToLogin() {
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        activity?.finish()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}