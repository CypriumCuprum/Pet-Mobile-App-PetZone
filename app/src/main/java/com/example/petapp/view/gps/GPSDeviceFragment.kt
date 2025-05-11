package com.example.petapp.view.gps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petapp.R
import com.example.petapp.data.model.GPSEntity
import com.example.petapp.viewmodel.GPSDeviceViewModel
import com.example.petapp.viewmodel.gps.GPSDeviceAdapter
import com.example.petapp.viewmodel.pet.PetViewModel
import com.example.petapp.viewmodel.user.LoginViewModel
import kotlinx.coroutines.launch

class GPSDeviceFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var addDeviceButton: AppCompatButton

    private lateinit var viewModel: GPSDeviceViewModel
    private lateinit var adapter: GPSDeviceAdapter
    private lateinit var petViewModel: PetViewModel
    private lateinit var gpsViewModel: GPSDeviceViewModel
    private lateinit var loginViewModel: LoginViewModel

    // Pet ID from arguments
    private var petIdList: List<String>? = null

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
        loginViewModel = ViewModelProvider(
            this,
            LoginViewModel.Factory(requireActivity().application)
        )[LoginViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_g_p_s_device, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        recyclerView = view.findViewById(R.id.gpsdevices_recycler_view)
        addDeviceButton = view.findViewById(R.id.buttonAddDevice)

        setupViewModel()
        setupRecyclerView()
        setupAddDeviceButton()
        loadGPSDevices()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[GPSDeviceViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = GPSDeviceAdapter(
            petRepository = petViewModel.getRepository(),
            onItemClick = { gpsDevice ->
                // Navigate to device details or map screen
                // You can implement this based on your app's navigation
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupAddDeviceButton() {
        addDeviceButton.setOnClickListener {
            val gpsDeviceDetailsFragment = DeviceDetailsFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, gpsDeviceDetailsFragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }
    }

    private fun loadGPSDevices() {
        lifecycleScope.launch {
            petIdList = loginViewModel.getLoggedInUserId()?.let {
                petViewModel.getPetIdListByUserId(it)
            }
            val gpsDeviceList = petIdList?.flatMap { petId ->
                gpsViewModel.getGPSDevicesForPet(petId)
            } ?: emptyList()
            gpsDeviceList.forEach { gpsDevice ->
                gpsViewModel.getGPSDeviceInfoByAPI(gpsDevice.id)
            }
            val gpsDeviceListUpdate = petIdList?.flatMap { petId ->
                gpsViewModel.getGPSDevicesForPet(petId)
            } ?: emptyList()
            adapter.submitList(gpsDeviceListUpdate)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(petId: String) =
            GPSDeviceFragment().apply {
                arguments = Bundle().apply {
                    putString("pet_id", petId)
                }
            }
    }
}