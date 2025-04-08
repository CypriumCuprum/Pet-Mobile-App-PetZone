package com.example.petapp.view.petHealth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.petapp.R

class PetHealth : Fragment() {

    private lateinit var checkFoodButton: LinearLayout
    private lateinit var foodCheckForm: LinearLayout
    private lateinit var cancelButton: Button
    private lateinit var submitButton: Button
    private lateinit var foodAmountInput: EditText
    private lateinit var feedingTimeInput: EditText
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

        return view
    }

    private fun initViews(view: View) {
        checkFoodButton = view.findViewById(R.id.check_food_button)
        foodCheckForm = view.findViewById(R.id.food_check_form)
        cancelButton = view.findViewById(R.id.cancel_button)
        submitButton = view.findViewById(R.id.submit_button)
        foodAmountInput = view.findViewById(R.id.food_amount_input)
        feedingTimeInput = view.findViewById(R.id.feeding_time_input)
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

        // Submit button processes the inputs and closes the form
        submitButton.setOnClickListener {
            val foodAmount = foodAmountInput.text.toString()
            val feedingTime = feedingTimeInput.text.toString()

            // Process the inputs
            processInputs(foodAmount, feedingTime)

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
    }

    private fun processInputs(foodAmount: String, feedingTime: String) {
        // Update UI based on inputs
        if (foodAmount.isNotEmpty()) {
            foodStatus.text = "Fed $foodAmount g"
            lastFoodStatus.text = "Last Fed ($feedingTime)"

            // You can add code here to save the data to your database or shared preferences
            // For example:
            // saveFoodData(foodAmount, feedingTime)
        }
    }
}