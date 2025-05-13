package com.example.petapp.view.petHealth

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petapp.BuildConfig
import com.example.petapp.R
import com.example.petapp.data.model.PetStatisticEntity
import com.example.petapp.viewmodel.pet.PetViewModel
import com.example.petapp.viewmodel.statistic.PetStatisticViewModel
import com.example.petapp.viewmodel.statistic.StatisticTypeViewModel
import com.example.petapp.viewmodel.statistic.TestAdapter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.UUID

//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
private const val ARG_PETID = "petId"

class PetHealth : Fragment() {
    private lateinit var petStatisticViewModel: PetStatisticViewModel
    private lateinit var petViewModel: PetViewModel
    private lateinit var checkFoodButton: LinearLayout
    private lateinit var foodCheckForm: LinearLayout
    private lateinit var cancelButton: Button
    private lateinit var submitButton: Button
    private lateinit var foodAmountInput: EditText
    private lateinit var feedingTimeInput: EditText
    private lateinit var foodNoteInput: EditText
    private lateinit var foodStatus: TextView
    private lateinit var lastFoodStatus: TextView
    private lateinit var rootView: View
    private lateinit var petName: TextView
    private lateinit var petBreed: TextView
    private lateinit var petGender: ImageView
    private lateinit var framePetInformation: TextView
    private lateinit var ageText: TextView
    private lateinit var weightText: TextView
    private lateinit var heightText: TextView
    private lateinit var colorText: TextView
    private lateinit var petImage: ImageView
    private lateinit var layoutPetHealth: LinearLayout
    private var petId: String? = null
    private var statisticTypeId = "11111111-1111-1111-1111-111111111112"
    private lateinit var barChart: BarChart
    private val foodStatisticHelper = FoodStatisticHelper()
    private lateinit var toolbarIcon: ImageView
    private lateinit var toolbarTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        petId = arguments?.getString(ARG_PETID)
        println("Received petId: $petId")
        Log.d("Receive PetId: ", petId.toString())
        petViewModel = ViewModelProvider(
            this,
            PetViewModel.Factory(requireActivity().application)
        )[PetViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pet_health, container, false)
        rootView = view

        // Initialize views
        initViews(view)

        // Set up click listeners
        setupListeners()

        // Set up touch listener to dismiss keyboard
        setupTouchListener()
        toolbarIcon.setImageResource(R.drawable.bell_notification)
        toolbarTitle.text = "Pet Health"
        layoutPetHealth.visibility = View.GONE
        return view
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TestAdapter
    private lateinit var viewModel: StatisticTypeViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerStatistic)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TestAdapter(emptyList())
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[StatisticTypeViewModel::class.java]

//        viewModel.getAll().observe(viewLifecycleOwner) { list ->
//            adapter.setData(list)
//        }

        petStatisticViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[PetStatisticViewModel::class.java]

        setPet()

        setupObservers()
        petStatisticViewModel.loadFoodStatistics(petId)
    }

    private fun initViews(view: View) {
        checkFoodButton = view.findViewById(R.id.check_food_button)
        foodCheckForm = view.findViewById(R.id.food_check_form)
        cancelButton = view.findViewById(R.id.cancel_button)
        submitButton = view.findViewById(R.id.submit_button)
        foodAmountInput = view.findViewById(R.id.food_amount_input)
        feedingTimeInput = view.findViewById(R.id.feeding_time_input)
        foodNoteInput = view.findViewById(R.id.note_input)
        foodStatus = view.findViewById(R.id.food_status)
        lastFoodStatus = view.findViewById(R.id.last_food_status)
        petName = view.findViewById(R.id.pet_name)
        petBreed = view.findViewById(R.id.pet_breed)
        petGender = view.findViewById(R.id.pet_gender)
        framePetInformation = view.findViewById(R.id.frame_pet_information)
        ageText = view.findViewById(R.id.age_text)
        weightText = view.findViewById(R.id.weight_text)
        heightText = view.findViewById(R.id.height_text)
        colorText = view.findViewById(R.id.color_text)
        petImage = view.findViewById(R.id.pet_image)
        layoutPetHealth = view.findViewById(R.id.layout_pet_health_visiable)
        barChart = view.findViewById(R.id.food_chart)
        toolbarIcon = requireActivity().findViewById(R.id.right_icon_toolbar)
        toolbarTitle = requireActivity().findViewById(R.id.toolbar_title)
    }

    private fun setPet(){
        Log.d("Received PetID: ", petId.toString())
        petId?.let { id ->
            viewLifecycleOwner.lifecycleScope.launch {
                val pet = petViewModel.getPetById(id)
                // xử lý pet
                petName.text = pet.name
                petBreed.text = pet.breedName
                framePetInformation.text = pet.name + "'s Information"
                ageText.text = calculateAge(pet.birthDate.toString())
                weightText.text = pet.weight.toString() + " kg"
                heightText.text = pet.height.toString() + " cm"
                colorText.text = pet.color

                if(pet.gender=="male" || pet.gender=="Male"){
                    petGender.setImageResource(R.drawable.man)
                } else {
                    petGender.setImageResource(R.drawable.woman)
                }

                if (!pet.imageUrl.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(pet.imageUrl)
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.profile)
                        .into(petImage)
                } else {
                    println("Pet image URL is empty for pet ID: ${pet.id}")
//                petImage.setBackgroundResource(R.drawable.profile)
                }

                val petStatistic = petStatisticViewModel.getLatestPetStatistic(id, UUID.fromString(statisticTypeId))
                if (petStatistic != null) {
                    val timeSinceFed = getTimeSinceFed(petStatistic.recorded_at)
                    lastFoodStatus.text = "Last Fed (${timeSinceFed})"
                } else {
                    lastFoodStatus.text = "No feeding record found."
                }
            }
        }

    }
    fun calculateAge(birthDateStr: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val birthDate = LocalDate.parse(birthDateStr, formatter)
        val currentDate = LocalDate.now()

        val period = Period.between(birthDate, currentDate)
        val years = if (period.years > 0) "${period.years}y" else ""
        val months = if (period.months > 0) "${period.months}m" else ""
        val days = if (period.days > 0) "${period.days}d" else ""

        return listOf(years, months, days)
            .filter { it.isNotEmpty() }
            .joinToString(" ")
//        return "${period.years}Y ${period.months}M ${period.days}D"
    }

    fun getTimeSinceFed(recordedAt: String): String {
        // Định dạng ngày giờ: dd/MM/yyyy HH:mm
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

        // Chuyển đổi chuỗi recorded_at thành LocalDateTime
        val recordedDateTime = LocalDateTime.parse(recordedAt, formatter)

        // Lấy thời gian hiện tại
        val currentDateTime = LocalDateTime.now()

        // Tính sự khác biệt giữa thời gian hiện tại và recorded_at
        val duration = Duration.between(recordedDateTime, currentDateTime)

        // Lấy số ngày, giờ, phút từ sự khác biệt
        val days = duration.toDays()
        val hours = duration.toHours() % 24
        val minutes = duration.toMinutes() % 60

        // Trả về chuỗi tương ứng với sự khác biệt
        return when {
            days > 0 -> "$days day(s) ago"
            hours > 0 -> "$hours hour(s) ago"
            minutes > 0 -> "$minutes minute(s) ago"
            else -> "Just now"
        }
    }

    private fun setFood(){
    }

    private fun setupListeners() {
        // Show form when Check Food button is clicked
        checkFoodButton.setOnClickListener {
            foodCheckForm.visibility = View.VISIBLE
        }

        // Cancel button closes the form
        cancelButton.setOnClickListener {
            foodCheckForm.visibility = View.GONE
            clearInputs()
            hideKeyboard()
        }

        feedingTimeInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                    val selectedDateTime = Calendar.getInstance().apply {
                        set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute)
                    }

                    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    feedingTimeInput.setText(format.format(selectedDateTime.time))
                }, hour, minute, true).show()

            }, year, month, day).show()
        }

        // Submit button processes the inputs and closes the form
        submitButton.setOnClickListener {
            val foodAmount = foodAmountInput.text.toString()
            val feedingTime = feedingTimeInput.text.toString()
            val foodNote = foodNoteInput.text.toString()
            // Process the inputs
            processInputs(foodAmount, feedingTime, foodNote)

            // Hide the form and clear inputs
            foodCheckForm.visibility = View.GONE
            clearInputs()
            hideKeyboard()
        }
    }

    private fun setupTouchListener() {
        // Setup touch listener for non-text box views to hide keyboard
        rootView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Check if the event was outside the text fields
                if (!isTouchInsideView(event, foodAmountInput) && !isTouchInsideView(event, feedingTimeInput)) {
                    hideKeyboard()
                }
            }
            false
        }
    }

    private fun isTouchInsideView(event: MotionEvent, view: View): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1]

        return (event.rawX >= x && event.rawX <= (x + view.width) &&
                event.rawY >= y && event.rawY <= (y + view.height))
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = requireActivity().currentFocus
        if (currentFocusView != null) {
            imm.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
            currentFocusView.clearFocus()
        }
    }

    private fun clearInputs() {
        foodAmountInput.text.clear()
        feedingTimeInput.text.clear()
        foodNoteInput.text.clear()
    }

        private fun processInputs(foodAmount: String, feedingTime: String, foodNote: String) {
            // Update UI based on inputs
            if (foodAmount.isNotEmpty() and feedingTime.isNotEmpty()) {
//                foodStatus.text = "Fed $foodAmount g"
//                lastFoodStatus.text = "Last Fed ($feedingTime)"
                val foodStatistic = PetStatisticEntity(
                    value = foodAmount.toFloat(),
                    recorded_at = feedingTime,
                    petid = petId.toString(),
                    statistic_typeid = UUID.fromString(statisticTypeId),
                    note = foodNote
                )
                Log.d("PetId key food amount: ", petId.toString())
                Log.d("StatisticTypeId key food amount: ", statisticTypeId)
                Log.d("FoodStatistic", "FoodAmount: ${foodStatistic.value}, RecordedAt: ${foodStatistic.recorded_at}, PetId: ${foodStatistic.petid}, StatisticTypeId: ${foodStatistic.statistic_typeid}, Note: ${foodStatistic.note}")
                // You can add code here to save the data to your database or shared preferences
                // For example:
                // saveFoodData(foodAmount, feedingTime)
                petStatisticViewModel.insert(foodStatistic)
                petStatisticViewModel.loadFoodStatistics(petId)
                setupObservers()
                Toast.makeText(context, "Import successful", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupObservers() {
        // Observer cho dữ liệu thống kê thức ăn
        petStatisticViewModel.foodStatistics.observe(viewLifecycleOwner) { statistics ->
            if (statistics.isNotEmpty()) {
                setupFoodChart(statistics)
            }
        }
    }

    private fun setupFoodChart(statistics: List<PetStatisticEntity>) {
        // Xử lý dữ liệu để hiển thị biểu đồ
        val groupedData = foodStatisticHelper.groupFoodStatisticsByDay(statistics)
        val last7DaysData = foodStatisticHelper.getLast7DaysData(groupedData)

        // Tạo entries cho biểu đồ
        val entries = ArrayList<BarEntry>()
        val xAxisLabels = ArrayList<String>()

        // Thêm dữ liệu vào entries
        for (i in last7DaysData.indices) {
            val (date, value) = last7DaysData[i]
            entries.add(BarEntry(i.toFloat(), value))
            xAxisLabels.add(foodStatisticHelper.getDayName(date))
        }

        // Tạo dataset
        val dataSet = BarDataSet(entries, "Food Consumption (g)")
        dataSet.color = resources.getColor(R.color.bg_main_color, null)
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 12f

        // Tạo data từ dataset
        val barData = BarData(dataSet)
        barData.barWidth = 0.7f

        // Cấu hình barChart
        barChart.apply {
            data = barData
            description.isEnabled = false
            legend.isEnabled = true
            setFitBars(true)
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)

            // Cấu hình trục X
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(xAxisLabels)
                setDrawGridLines(false)
            }

            // Cấu hình trục Y bên trái
            axisLeft.apply {
                axisMinimum = 0f
                granularity = 50f
                setDrawGridLines(true)
            }

            // Cấu hình trục Y bên phải
            axisRight.isEnabled = false

            // Hiển thị animation
            animateY(1000)

            // Cập nhật biểu đồ
            invalidate()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PetHealth.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            PetHealth().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                    putString(ARG_PETID, param1)
                }
            }
    }
}