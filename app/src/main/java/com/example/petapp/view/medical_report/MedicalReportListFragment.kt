package com.example.petapp.view.medical_report

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petapp.R
import com.example.petapp.data.model.submodel.MedicalReportExtend
import com.example.petapp.viewmodel.medicalreport.MedicalReportViewModel
import com.example.petapp.viewmodel.pet.PetViewModel
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val PREFS_NAME = "PetAppPrefs"
private const val KEY_USER_ID = "logged_in_user_id"

class MedicalReportListFragment : Fragment(), OnReportItemClickListener {
    private lateinit var editTextStartDate: TextView
    private lateinit var editTextEndDate: TextView
    private lateinit var recyclerViewMedicalReportList: RecyclerView
    private lateinit var btnAddNewMedicalReport: AppCompatButton
    private lateinit var medicalReportListAdapter: MedicalReportListAdapter // Adapter instance
    private lateinit var medicalReportViewModel: MedicalReportViewModel
    private lateinit var petViewModel: PetViewModel
    private lateinit var sharedPreferences: SharedPreferences

    // Store selected dates as Calendar objects
    private var selectedStartDate: Calendar? = null
    private var selectedEndDate: Calendar? = null

    // Date Formats
    private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val parseDateFormat =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Assumes createdAt is dd/MM/yyyy
    private val monthYearFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())

    // Placeholder for all reports (replace with actual data fetching)
    private var allMedicalReports: List<MedicalReportExtend> = emptyList()
    private var clickedReportId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewModel
        medicalReportViewModel = ViewModelProvider(
            this,
            MedicalReportViewModel.Factory(requireActivity().application)
        )[MedicalReportViewModel::class.java]
        petViewModel = ViewModelProvider(
            this,
            PetViewModel.Factory(requireActivity().application)
        )[PetViewModel::class.java]
        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medical_report_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViews(view)
        setupRecyclerView()
        setupButtonClickListeners()
        loadMedicalReports() // Load initial data
    }

    private fun findViews(view: View) {
        editTextStartDate = view.findViewById(R.id.editTextStartDate)
        editTextEndDate = view.findViewById(R.id.editTextEndDate)
        recyclerViewMedicalReportList = view.findViewById(R.id.recyclerViewMedicalReportList)
        btnAddNewMedicalReport = view.findViewById(R.id.buttonAddNew)
    }

    private fun setupRecyclerView() {
        // Initialize adapter with an empty list
        medicalReportListAdapter = MedicalReportListAdapter(emptyList(), this)
        recyclerViewMedicalReportList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = medicalReportListAdapter
        }
    }

    private fun setupButtonClickListeners() {
        btnAddNewMedicalReport.setOnClickListener {
            navigateToAddFragment()
        }

        editTextStartDate.setOnClickListener {
            showDatePickerDialog(editTextStartDate) { calendar ->
                selectedStartDate = calendar
                editTextStartDate.text = displayDateFormat.format(calendar.time)
                filterAndDisplayReports() // Re-filter when date changes
            }
        }

        editTextEndDate.setOnClickListener {
            showDatePickerDialog(editTextEndDate) { calendar ->
                // Ensure end date is not before start date
                if (selectedStartDate != null && calendar.before(selectedStartDate)) {
                    Toast.makeText(
                        requireContext(),
                        "End date cannot be before start date",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Set time to end of day for inclusive range
                    calendar.set(Calendar.HOUR_OF_DAY, 23)
                    calendar.set(Calendar.MINUTE, 59)
                    calendar.set(Calendar.SECOND, 59)
                    calendar.set(Calendar.MILLISECOND, 999)

                    selectedEndDate = calendar
                    editTextEndDate.text = displayDateFormat.format(calendar.time)
                    filterAndDisplayReports() // Re-filter when date changes
                }
            }
        }
    }

    // Modified Date Picker to accept a callback
    private fun showDatePickerDialog(textView: TextView, onDateSelected: (Calendar) -> Unit) {
        val currentSelection =
            if (textView == editTextStartDate) selectedStartDate else selectedEndDate
        val calendar = currentSelection ?: Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val newSelectedDate = Calendar.getInstance()
                newSelectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)
                // Clear time part for start date comparison consistency
                if (textView == editTextStartDate) {
                    newSelectedDate.set(Calendar.HOUR_OF_DAY, 0)
                    newSelectedDate.set(Calendar.MINUTE, 0)
                    newSelectedDate.set(Calendar.SECOND, 0)
                    newSelectedDate.set(Calendar.MILLISECOND, 0)
                }
                onDateSelected(newSelectedDate) // Use the callback
            },
            year,
            month,
            day
        )

        // Optional: Set date limits (e.g., end date picker cannot be before start date)
        // if (textView == editTextEndDate && selectedStartDate != null) {
        //    datePickerDialog.datePicker.minDate = selectedStartDate!!.timeInMillis
        // }
        datePickerDialog.datePicker.maxDate =
            System.currentTimeMillis() // Cannot select future dates

        datePickerDialog.show()
    }

    private fun loadMedicalReports() {
        val userId = sharedPreferences.getString(KEY_USER_ID, null)
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
//        lifecycleScope.launch {
//            // Fetch all medical reports for the logged-in user
//            val petList = petViewModel.getPetsForHome(userId)
//            val petMap: Map<String, String> = petList.associate { it.id to it.name }
//            println("Pet List: $petList")
//
//            allMedicalReports = medicalReportViewModel.getAllMedicalReportByPetIdList(
//                petIdList = petList.map { it.id },
//                petName = petMap
//            )
//            filterAndDisplayReports()
//        }
        //v2
        lifecycleScope.launch {
//            allMedicalReports = medicalReportViewModel.getAllMedicalReportByUserId(userId)
            val groupedListItems =
                medicalReportViewModel.getAllMedicalReportByUserIdWithFilterAndSort(
                    userId = userId,
                    numItem = 1,
                    page = 0,
                    startDate = "01/01/2000",
                    endDate = "31/12/2099"
                ) // Fetch all reports
//            filterAndDisplayReports()
            medicalReportListAdapter.updateData(groupedListItems)
        }
        println("All Medical Reports: $allMedicalReports")
//        allMedicalReports = getDummyMedicalReports() // Use dummy data for now

//        filterAndDisplayReports()
    }

    private fun filterAndDisplayReports() {
        val filteredReports = allMedicalReports.filter { report ->
            try {
                val reportDateCal = Calendar.getInstance()
                val reportDate = parseDateFormat.parse(report.createdAt)
                if (reportDate != null) {
                    reportDateCal.time = reportDate
                    // Clear time part for consistent date comparison
                    reportDateCal.set(Calendar.HOUR_OF_DAY, 0)
                    reportDateCal.set(Calendar.MINUTE, 0)
                    reportDateCal.set(Calendar.SECOND, 0)
                    reportDateCal.set(Calendar.MILLISECOND, 0)


                    val afterStartDate =
                        selectedStartDate == null || !reportDateCal.before(selectedStartDate)
                    val beforeEndDate =
                        selectedEndDate == null || !reportDateCal.after(selectedEndDate)

                    // Log.d("Filtering", "Report Date: ${displayDateFormat.format(reportDateCal.time)}, Start: ${selectedStartDate?.let { displayDateFormat.format(it.time) }}, End: ${selectedEndDate?.let { displayDateFormat.format(it.time) }}, AfterStart: $afterStartDate, BeforeEnd: $beforeEndDate")


                    return@filter afterStartDate && beforeEndDate
                }
            } catch (e: ParseException) {
                e.printStackTrace() // Log error
            }
            false // Exclude if date parsing fails
        }
        filteredReports.forEach {
            println("Filtered Report: ${it.title}, Date: ${it.createdAt}")
        }
        // Group the filtered reports
        val groupedListItems = groupReportsByMonth(filteredReports)

        // Update the adapter
        medicalReportListAdapter.updateData(groupedListItems)
    }


    private fun groupReportsByMonth(reports: List<MedicalReportExtend>): List<ListItem> {
        if (reports.isEmpty()) {
            return emptyList()
        }

        // 1. Sort reports by date (newest first)
        val sortedReports = reports.sortedByDescending { report ->
            try {
                parseDateFormat.parse(report.createdAt)
                    ?: Date(0) // Handle parsing error, sort failed ones early
            } catch (e: ParseException) {
                Date(0) // Place invalid dates at the beginning/end
            }
        }

        val groupedList = mutableListOf<ListItem>()
        var lastMonthYear: String? = null

        // 2. Iterate and add headers
        for (report in sortedReports) {
            try {
                val date = parseDateFormat.parse(report.createdAt)
                if (date != null) {
                    val currentMonthYear = monthYearFormat.format(date)
                    // Add header if month/year changes
                    if (currentMonthYear != lastMonthYear) {
                        groupedList.add(ListItem.HeaderItem(currentMonthYear))
                        lastMonthYear = currentMonthYear
                    }
                    // Add the report item
                    groupedList.add(ListItem.ReportItem(report))
                }
            } catch (e: ParseException) {
                // Optionally handle reports with invalid dates differently
                // Maybe add them to a separate section or log an error
                e.printStackTrace()
            }
        }

        return groupedList
    }


    // --- Navigation ---
    private fun navigateToAddFragment() {
        val fragment = MedicalReportDetailFragment() // Assuming this is your add/edit screen
        val toolbarView = requireActivity().findViewById<View>(R.id.toolbar)
        val toolbarTextView = toolbarView.findViewById<TextView>(R.id.toolbar_title)
        toolbarTextView.text = "Add New Medical Report" // More appropriate title
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null) // Allows user to navigate back to the list
        transaction.commit()
    }

    private fun navigateToEditFragment() {
        val fragment =
            MedicalReportDetailEditFragment(clickedReportId) // Assuming this is your add/edit screen
        val toolbarView = requireActivity().findViewById<View>(R.id.toolbar)
        val toolbarTextView = toolbarView.findViewById<TextView>(R.id.toolbar_title)
        toolbarTextView.text = "Edit Medical Report" // More appropriate title
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null) // Allows user to navigate back to the list
        transaction.commit()
    }

    // --- Dummy Data (Replace with real data source) ---
    private fun getDummyMedicalReports(): List<MedicalReportExtend> {
        return listOf(
            MedicalReportExtend(
                "1",
                "Annual Checkup",
                "City Vet Clinic",
                "Dr. Smith",
                "Routine checkup, all clear.",
                "15/05/2025",
                "Buddy",
                listOf()
            ),
            MedicalReportExtend(
                "2",
                "Vaccination",
                "Animal Hospital Central",
                "Dr. Jones",
                "Booster shots administered.",
                "15/05/2025",
                "Lucy",
                listOf()
            ),
            MedicalReportExtend(
                "3",
                "Injury Check",
                "City Vet Clinic",
                "Dr. Smith",
                "Minor paw injury, treated.",
                "02/05/2025",
                "Buddy",
                listOf()
            ),
            MedicalReportExtend(
                "4",
                "Dental Cleaning",
                "PetCare Vets",
                "Dr. Davis",
                "Scale and polish performed.",
                "25/04/2024",
                "Max",
                listOf()
            ),
            MedicalReportExtend(
                "5",
                "Allergy Test",
                "Animal Hospital Central",
                "Dr. Jones",
                "Testing for food allergies.",
                "10/04/2024",
                "Lucy",
                listOf()
            ),
            MedicalReportExtend(
                "6",
                "Follow-up",
                "City Vet Clinic",
                "Dr. Smith",
                "Paw healing well.",
                "18/05/2024",
                "Buddy",
                listOf()
            ),
            MedicalReportExtend(
                "7",
                "X-Ray",
                "PetCare Vets",
                "Dr. Davis",
                "Checked for bone issues.",
                "30/03/2024",
                "Max",
                listOf()
            )
        )
    }

    //on Resume
    override fun onResume() {
        super.onResume()
        // Reload data when fragment is resumed
        loadMedicalReports()
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MedicalReportListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onReportItemClicked(reportId: String) {
        this.clickedReportId = reportId
        Toast.makeText(requireContext(), "Clicked Report ID: $reportId", Toast.LENGTH_SHORT).show()
        Log.d("MedicalReportList", "Clicked Report ID stored: ${this.clickedReportId}")
        // Navigate to the detail fragment with the clicked report ID
        navigateToEditFragment()
    }
}