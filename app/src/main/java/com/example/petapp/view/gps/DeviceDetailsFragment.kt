package com.example.petapp.view.gps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petapp.R
import com.example.petapp.view.YourPetAdapter
import com.example.petapp.viewmodel.pet.PetViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DeviceDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeviceDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var recyclerViewHorizontalYourPet: RecyclerView
    private lateinit var petViewModel: PetViewModel
    private lateinit var yourpetAdapter: YourPetAdapter

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
        return inflater.inflate(R.layout.fragment_device_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        recyclerViewHorizontalYourPet = view.findViewById(R.id.recyclerViewHorizontal)

        setupYourPetRecyclerView()

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DeviceDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DeviceDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}