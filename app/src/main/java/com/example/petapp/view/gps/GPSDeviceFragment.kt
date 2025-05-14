package com.example.petapp.view.gps

import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
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

        setupRecyclerView()
        setupAddDeviceButton()
        loadGPSDevices()
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
        // Setup ItemTouchHelper for swipe-to-delete
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, // No drag and drop
            ItemTouchHelper.LEFT // Swipe left to delete
        ) {
            private val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.trash)
            private val background = ColorDrawable(Color.RED)

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // Not moving items
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val deviceToDelete = adapter.currentList[position]
                    showDeleteConfirmationDialog(deviceToDelete, position)
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - (deleteIcon?.intrinsicHeight ?: 0)) / 2
                val iconTop =
                    itemView.top + (itemView.height - (deleteIcon?.intrinsicHeight ?: 0)) / 2
                val iconBottom = iconTop + (deleteIcon?.intrinsicHeight ?: 0)

                if (dX < 0) { // Swiping to the left
                    val iconLeft = itemView.right - iconMargin - (deleteIcon?.intrinsicWidth ?: 0)
                    val iconRight = itemView.right - iconMargin
                    deleteIcon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                    background.setBounds(
                        itemView.right + dX.toInt(), // Start from swiped edge
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                } else { // view is unSwiped
                    background.setBounds(0, 0, 0, 0)
                    deleteIcon?.setBounds(0, 0, 0, 0)
                }
                background.draw(c)
                deleteIcon?.draw(c)
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }

    private fun showDeleteConfirmationDialog(device: GPSEntity, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Device")
            .setMessage("Are you sure you want to delete '${device.name}'?")
            .setPositiveButton("Delete") { dialog, _ ->
                performDelete(device)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                // Reset swipe state by notifying item changed
                adapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .setOnCancelListener {
                // Also reset if dialog is dismissed (e.g. by back button)
                adapter.notifyItemChanged(position)
            }
            .show()
    }

    private fun performDelete(device: GPSEntity) {
        lifecycleScope.launch {
            val result = gpsViewModel.deleteGPSDevice(device.id)
            if (result > 0) { // Assuming deleteGPSDevice returns num rows affected
                Toast.makeText(
                    requireContext(),
                    "'${device.name}' deleted successfully.",
                    Toast.LENGTH_SHORT
                ).show()
                // Reload all devices (can be slightly less efficient but simpler)
                // loadGPSDevices()

                // Update the current list and submit (more efficient for ListAdapter)
                val currentList = adapter.currentList.toMutableList()
                val removed = currentList.remove(device)
                if (removed) {
                    adapter.submitList(currentList)
                } else {
                    // If remove failed (e.g. item not found, race condition), reload all as a fallback
                    loadGPSDevices()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Failed to delete '${device.name}'.",
                    Toast.LENGTH_SHORT
                ).show()
                // Potentially refresh the item to reset its visual state if deletion failed
                val currentPosition = adapter.currentList.indexOf(device)
                if (currentPosition != -1) {
                    adapter.notifyItemChanged(currentPosition)
                } else {
                    loadGPSDevices() // Fallback if item not found in current adapter list
                }
            }
        }
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