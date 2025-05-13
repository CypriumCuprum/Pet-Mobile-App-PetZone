package com.example.petapp.view.gps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petapp.R
import com.example.petapp.data.model.PetEntity
import com.example.petapp.viewmodel.pet.SelectPetAdapter
import com.example.petapp.viewmodel.pet.PetViewModel
import com.example.petapp.viewmodel.user.LoginViewModel
import kotlinx.coroutines.launch
import android.widget.Toast
import com.example.petapp.data.model.GPSEntity
import com.example.petapp.viewmodel.GPSDeviceViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A fragment to display GPS device details and select a pet to associate with the device.
 * Use the [DeviceDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeviceDetailsFragment : Fragment() {

    private lateinit var recyclerViewHorizontalYourPet: RecyclerView
    private lateinit var petViewModel: PetViewModel
    private lateinit var selectPetAdapter: SelectPetAdapter
    private lateinit var buttonAddDevice: AppCompatButton
    private lateinit var gpsViewModel: GPSDeviceViewModel
    private lateinit var editTextDeviceName: EditText
    private lateinit var editTextDeviceID: EditText

    // The selected pet for this device
    private var selectedPet: PetEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        petViewModel = ViewModelProvider(
            this,
            PetViewModel.Factory(requireActivity().application)
        )[PetViewModel::class.java]
        gpsViewModel = ViewModelProvider(
            this,
            GPSDeviceViewModel.Factory(requireActivity().application)
        )[GPSDeviceViewModel::class.java]
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
        buttonAddDevice = view.findViewById(R.id.buttonSaveDevice)
        editTextDeviceName = view.findViewById(R.id.editTextDeviceName)
        editTextDeviceID = view.findViewById(R.id.editTextDeviceID)

        setupPetRecyclerView()
        loadPets()
        setupAddDeviceButton()
    }

    private fun setupAddDeviceButton() {
        buttonAddDevice.setOnClickListener {
            // Handle add device button click
            if (selectedPet != null) {
                // Proceed with adding the device for the selected pet
                val gpsDevice = GPSEntity(
                    id = editTextDeviceID.text.toString().trim(),
                    name = editTextDeviceName.text.toString().trim(),
                    petId = selectedPet!!.id,
                )

                lifecycleScope.launch {
                    var res_create: Long = gpsViewModel.addGPSDevice(gpsDevice)
                    if (res_create >= 0) {
                        println("GPS device added successfully with ID: $res_create")
                    } else {
                        println("Failed to add GPS device")
                    }
                }
                Toast.makeText(
                    requireContext(),
                    "Device added for pet: ${selectedPet!!.name}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please select a pet first",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupPetRecyclerView() {
        // Initialize the adapter with selection callback
        selectPetAdapter = SelectPetAdapter { pet ->
            // Handle pet selection
            onPetSelected(pet)
        }

        // Setup RecyclerView
        recyclerViewHorizontalYourPet.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = selectPetAdapter
        }

        println("GPS Pet RecyclerView setup complete with layout manager: ${recyclerViewHorizontalYourPet.layoutManager}")
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

        if (userId != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val pets = petViewModel.getPetsForHome(userId)
                    println("Loaded pets for GPS device: ${pets.size} pets")
                    selectPetAdapter.submitList(pets)

                    // If we previously had a selected pet, try to reselect it
                    if (selectedPet != null) {
                        val selectedPetIndex = pets.indexOfFirst { it.id == selectedPet?.id }
                        if (selectedPetIndex >= 0) {
                            selectPetAdapter.selectPet(selectedPetIndex)
                        }
                    }
                } catch (e: Exception) {
                    println("Error loading pets: ${e.message}")
                }
            }
        } else {
            // Handle case where user ID is null
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onPetSelected(pet: PetEntity) {
        // Store the selected pet for future use
        selectedPet = pet

        // Display a confirmation message
        Toast.makeText(
            requireContext(),
            "Selected pet: ${pet.name}",
            Toast.LENGTH_SHORT
        ).show()

        // Here you can perform any other actions needed when a pet is selected
        println("Selected pet ID: ${pet.id}, Name: ${pet.name}")

        // Example: Update other UI elements with the selected pet's information
        // updateDeviceInfoWithSelectedPet(pet)
    }

    // Method to get the currently selected pet (can be called from parent activity/fragment)
    fun getSelectedPet(): PetEntity? {
        return selectedPet
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