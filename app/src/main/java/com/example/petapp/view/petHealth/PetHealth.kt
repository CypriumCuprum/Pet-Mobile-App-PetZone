package com.example.petapp.view.petHealth

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petapp.BuildConfig
import com.example.petapp.R
import com.example.petapp.data.model.PetStatisticEntity
import com.example.petapp.viewmodel.statistic.StatisticTypeViewModel
import com.example.petapp.viewmodel.statistic.TestAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class PetHealth : Fragment() {

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
        val imageView = view.findViewById<ImageView>(R.id.itemImage123)
        val imageUrl = "${BuildConfig.SERVER_URL}static/images/items/22836b0c-43de-4975-8afd-e05f6e8e85d8.png"

        Glide.with(this)
            .load(imageUrl)
            .into(imageView)

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

        viewModel.getAll().observe(viewLifecycleOwner) { list ->
            adapter.setData(list)
        }
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
        if (foodAmount.isNotEmpty()) {
            foodStatus.text = "Fed $foodAmount g"
            lastFoodStatus.text = "Last Fed ($feedingTime)"
            val foodStatistic = PetStatisticEntity(
                value = foodAmount.toFloat(),
                recorded_at = feedingTime,
                petid = UUID.fromString("11111111-1111-1111-1111-111111111112"),
                statistic_typeid = UUID.fromString("11111111-1111-1111-1111-111111111112"),
                note = foodNote
            )
            // You can add code here to save the data to your database or shared preferences
            // For example:
            // saveFoodData(foodAmount, feedingTime)
        }
    }
}