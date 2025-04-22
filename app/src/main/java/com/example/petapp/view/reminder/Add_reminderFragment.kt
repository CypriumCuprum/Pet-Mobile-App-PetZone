package com.example.petapp.view.reminder

import android.app.AlertDialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.petapp.R
import com.example.petapp.data.model.ReminderEntity
import com.example.petapp.databinding.FragmentAddReminderBinding
import com.example.petapp.viewmodel.reminder.RemindersViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Add_reminder.newInstance] factory method to
 * create an instance of this fragment.
 */

class Add_reminderFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var viewModel: RemindersViewModel
    private lateinit var binding: FragmentAddReminderBinding
    private lateinit var reminderTypeButtons: List<MaterialCardView>
    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Select date")
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .setCalendarConstraints(
            CalendarConstraints.Builder()
                .setValidator(com.google.android.material.datepicker.DateValidatorPointForward.now())
                .build()
        )
        .build()
    private lateinit var dateButton: MaterialCardView
    private lateinit var reminder:ReminderEntity
    private lateinit var timeButton: MaterialCardView
    private lateinit var repeatButton: MaterialCardView
    private lateinit var buttonSaveReminder: MaterialButton
    private lateinit var date1: String
    private lateinit var time1: String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddReminderBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[RemindersViewModel::class.java]
        reminder = ReminderEntity(
            type = "feed",
            timeReminder = "",
            repeat = "Never",
            note = "",
            status = true
        )
        reminderTypeButtons = listOf(
            binding.btnFeeding,
            binding.btnMedicine,
            binding.btnWalking,
            binding.btnGrooming,
            binding.btnVetVisit
        )
        setupReminderTypeButtons()

        dateButton = binding.selectDate
        dateButton.setOnClickListener{
           datePicker.show(childFragmentManager, "DATE_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener { selectedDate ->
            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(java.util.Date(selectedDate))
            println("Ngày được chọn:" +  formattedDate)
            val dateSelected = binding.dateSelected
            dateSelected.text = formattedDate
            println("Nhắc nhở đang được tạo" +  reminder)
            date1 = formattedDate
        }

        timeButton = binding.selectTime
        timeButton.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Time")
                .build()

            timePicker.show(childFragmentManager, "TIME_PICKER")

            timePicker.addOnPositiveButtonClickListener {
                val selectedHour = timePicker.hour
                val selectedMinute = timePicker.minute
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                println("Thời gian được chọn:" +  formattedTime)
                val timeSelected = binding.timeSelected
                timeSelected.text = formattedTime
                time1 = formattedTime
            }
        }



        repeatButton = binding.selectRepeat
        repeatButton.setOnClickListener {
            val repeatOptions = arrayOf("Never", "Daily", "Weekly", "Monthly", "Yearly")

            // Tạo popup menu
//            val wrapper = ContextThemeWrapper(requireContext(), com.google.android.material.R.style.Theme_AppCompat_Light)





            repeatButton.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.text_light_green))
            val textRepeatButton = binding.textRepeatButton
            textRepeatButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            binding.repeatIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))


            val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_repeat_options, null)
            val popupWindow = PopupWindow(
                popupView,
                resources.getDimensionPixelSize(R.dimen.popup_width), // Chiều rộng tùy chỉnh
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
            )


            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.popup_background))
            popupWindow.elevation = 10f


            val listView = popupView.findViewById<androidx.appcompat.widget.LinearLayoutCompat>(R.id.repeat_options_container)


            for (option in repeatOptions) {
                val optionView = LayoutInflater.from(requireContext()).inflate(R.layout.item_repeat_option, listView, false)
                val optionText = optionView.findViewById<TextView>(R.id.option_text)
                optionText.text = option
                if(option == reminder.repeat) {
                    optionText.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_light_green))
                    optionText.typeface = ResourcesCompat.getFont(requireContext(), R.font.fredoka_medium)
                } else {
                    optionText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                }


                optionView.setOnClickListener {
                    binding.repeatSelected.text = option
                    reminder.repeat = option.lowercase()
                    popupWindow.dismiss()
                }

                listView.addView(optionView)
            }


            popupWindow.setOnDismissListener {
                repeatButton.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                textRepeatButton.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                binding.repeatIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
            }


            popupWindow.showAsDropDown(binding.selectRepeat)
        }
        buttonSaveReminder = binding.btnSaveReminder
        buttonSaveReminder.setOnClickListener{
            val note = binding.etNote.text.toString()
            reminder.note = note
            if(!time1.isEmpty() && !date1.isEmpty() ){
                reminder.timeReminder = date1 + " " + time1
                viewModel.insertReminder(reminder)
                val remindersFragment = Reminder1Fragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frame_container, remindersFragment)
                    .addToBackStack(null) // nếu muốn quay lại fragment cũ
                    .commit()
            }
            else {
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Warning")
                alertDialog.setMessage("Please select time and date for the reminder")
                alertDialog.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                alertDialog.show()
            }
        }


    }
    private fun setupReminderTypeButtons() {
        val buttonTypes = mapOf(
            binding.btnFeeding to "feed",
            binding.btnMedicine to "medicine",
            binding.btnWalking to "walk",
            binding.btnGrooming to "groom",
            binding.btnVetVisit to "vet"
        )

        // Set click listeners for each button
        buttonTypes.forEach { (button, type) ->
            button.setOnClickListener {
                reminder.type = type
                updateButtonStates(button)
                // Optional: Update the view model with the selected type
            }
        }
    }

    private fun updateButtonStates(selectedButton: MaterialCardView) {
        // Update all buttons to reflect the current selection
        reminderTypeButtons.forEach { button ->
            if (button == selectedButton) {
                // Selected button gets green background
                button.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.text_light_green))

                // Update text color to white
                val textView = button.findViewWithTag<TextView>("buttonText")
                textView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                // Set stroke color to border_color_button_reminder
                button.strokeColor = ContextCompat.getColor(requireContext(), R.color.border_color_button_reminder)
                button.strokeWidth = 3 // You may need to define this dimension

                // Make text bold
                textView.typeface = ResourcesCompat.getFont(requireContext(), R.font.fredoka_medium)
            } else {
                // Non-selected buttons get white background
                button.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

                // Update text color to black
                val textView = button.findViewWithTag<TextView>("buttonText")
                textView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

                // Set stroke color to border_color_reminder
                button.strokeColor = ContextCompat.getColor(requireContext(), R.color.border_color_reminder)
                button.strokeWidth = 2 // You may need to define this dimension

                // Make text regular
                textView.typeface = ResourcesCompat.getFont(requireContext(), R.font.fredoka_regular)
            }
        }
    }
}