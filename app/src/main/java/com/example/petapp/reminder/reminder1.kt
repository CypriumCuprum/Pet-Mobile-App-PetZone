package com.example.petapp.reminder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.petapp.R
import com.google.android.material.switchmaterial.SwitchMaterial

class Reminder1Fragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Reminder1Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view  = inflater.inflate(R.layout.fragment_reminder1,container,false)
        val mySwitch = view.findViewById<SwitchMaterial>(R.id.switch_feeding1)

        // lắng nghe switch
        mySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // The switch is enabled/checked
            } else {
                // The switch is disabled
            }
        }
        return inflater.inflate(R.layout.fragment_reminder1, container, false)
    }
}
