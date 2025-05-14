package com.example.petapp.view.medical_report

import android.app.Dialog // Import Dialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.* // Import View and KeyEvent
import android.widget.EditText
import android.widget.ImageView // Import ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Import Glide
import com.example.petapp.R
import com.example.petapp.data.model.PetEntity
import com.example.petapp.viewmodel.medical_report.MedicalReportImageAdapter
import com.example.petapp.viewmodel.medical_report.OnImageLongClickListener // IMPORT THE INTERFACE
import com.example.petapp.viewmodel.medicalreport.MedicalReportViewModel
import com.example.petapp.viewmodel.pet.PetViewModel
import com.example.petapp.viewmodel.pet.SelectPetAdapter
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val PREFS_NAME = "PetAppPrefs"
private const val KEY_USER_ID = "logged_in_user_id"

// 1. Implement the interface
class MedicalReportDetailEditFragment(reportId: String?) : Fragment(), OnImageLongClickListener {
    private var reportId: String? = reportId
    private lateinit var recyclerViewHorizontalYourPet: RecyclerView
    private lateinit var recyclerViewImageMedicalReport: RecyclerView
    private lateinit var buttonAddImageMedicalReport: AppCompatButton
    private lateinit var selectPetAdapter: SelectPetAdapter
    private lateinit var buttonSave: AppCompatButton
    private lateinit var buttonRemove: AppCompatButton
    private var selectedPet: PetEntity? = null
    private lateinit var petViewModel: PetViewModel
    private lateinit var medicalReportViewModel: MedicalReportViewModel
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var selectedImageUris = mutableListOf<Pair<String?, Uri>>()
    private lateinit var medicalReportImageAdapter: MedicalReportImageAdapter

    private lateinit var editTextTitle: EditText
    private lateinit var editTextVeterinary: EditText
    private lateinit var editTextVeterinarian: EditText
    private lateinit var editTextVeterinarianPrescription: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        petViewModel = ViewModelProvider(
            this,
            PetViewModel.Factory(requireActivity().application)
        )[PetViewModel::class.java]
        medicalReportViewModel = ViewModelProvider(
            this,
            MedicalReportViewModel.Factory(requireActivity().application)
        )[MedicalReportViewModel::class.java]
        sharedPreferences = requireActivity().getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    selectedImageUris.add(Pair(null, it))
                    medicalReportImageAdapter.notifyItemInserted(selectedImageUris.size - 1)
                    recyclerViewImageMedicalReport.scrollToPosition(selectedImageUris.size - 1)
                    println("Selected image URI: $it")
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_medical_report_detail_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewHorizontalYourPet = view.findViewById(R.id.recyclerViewHorizontal)
        recyclerViewImageMedicalReport =
            view.findViewById(R.id.recyclerViewImageMedicalReport)
        buttonAddImageMedicalReport =
            view.findViewById(R.id.buttonAddImageMedicalReport)
        buttonSave = view.findViewById(R.id.buttonSave)
        buttonRemove = view.findViewById(R.id.buttonRemove)
        editTextTitle = view.findViewById(R.id.editTextTitle)
        editTextVeterinary = view.findViewById(R.id.editTextVeterinary)
        editTextVeterinarian = view.findViewById(R.id.editTextVeterinarian)
        editTextVeterinarianPrescription = view.findViewById(R.id.editTextVeterinarianPrescription)
        setupPetRecyclerView()
        loadMedicalReport()
        loadPets()
        onButtonAddImageMedicalReportClick()
        buttonSave.setOnClickListener {
            onButtonSaveClick()
        }
        buttonRemove.setOnClickListener {
            onButtonRemoveClick()
        }
    }

    private fun loadMedicalReport() {
        lifecycleScope.launch {
            try {
                val medicalReport = medicalReportViewModel.getMedicalReportById(reportId)
                selectedPet =
                    petViewModel.getPetById(
                        medicalReport.petId
                    )
                selectedImageUris =
                    medicalReportViewModel.getAllImageMedicalReportByMedicalReportId(
                        medicalReport.id
                    ).map { Pair(it.id, Uri.parse(it.imageUrl)) }.toMutableList()
                if (medicalReport != null) {
                    editTextTitle.setText(medicalReport.title)
                    editTextVeterinary.setText(medicalReport.hospital)
                    editTextVeterinarian.setText(medicalReport.veterinarian)
                    editTextVeterinarianPrescription.setText(medicalReport.description)
                }
            } catch (e: Exception) {
                println("Error loading medical report: ${e.message}")
            }
            setupImageMedicalReportRecyclerView()
        }
    }

    private fun checkInputFields(): Boolean {
        val title = editTextTitle.text.toString().trim()
        val veterinary = editTextVeterinary.text.toString().trim()
        val veterinarian = editTextVeterinarian.text.toString().trim()
        val veterinarianPrescription = editTextVeterinarianPrescription.text.toString().trim()

        if (title.isEmpty() || veterinary.isEmpty() || veterinarian.isEmpty() || veterinarianPrescription.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun onButtonRemoveClick() {
        lifecycleScope.launch {
            try {
                medicalReportViewModel.deleteMedicalReport(reportId.toString())
                Toast.makeText(requireContext(), "Medical report deleted", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                println("Error deleting medical report: ${e.message}")
            }
            requireActivity().supportFragmentManager.popBackStack()
        }


    }


    private fun onButtonSaveClick() {
        val selectedPetId = selectedPet?.id
        if (selectedPetId != null && checkInputFields()) {
            val title = editTextTitle.text.toString().trim()
            val veterinary = editTextVeterinary.text.toString().trim()
            val veterinarian = editTextVeterinarian.text.toString().trim()
            val veterinarianPrescription = editTextVeterinarianPrescription.text.toString().trim()
            val stringList: List<Pair<String?, String>> = selectedImageUris.map { pair ->
                Pair(pair.first, pair.second.toString())
            }
            println("StringList: $stringList")
            lifecycleScope.launch {
                try {
                    medicalReportViewModel.saveMedicalReport(
                        id = reportId,
                        title = title,
                        hospital = veterinary,
                        veterinarian = veterinarian,
                        description = veterinarianPrescription,
                        petId = selectedPetId,
                        imageUrlList = stringList
                    )
                } catch (e: Exception) {
                    println("Error saving medical report: ${e.message}")
                }
            }
        } else {
            Toast.makeText(requireContext(), "Please select a pet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onButtonAddImageMedicalReportClick() {
        buttonAddImageMedicalReport.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
    }

    private fun setupPetRecyclerView() {
        selectPetAdapter = SelectPetAdapter { pet ->
            onPetSelected(pet)
        }
        recyclerViewHorizontalYourPet.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = selectPetAdapter
        }
        println("GPS Pet RecyclerView setup complete with layout manager: ${recyclerViewHorizontalYourPet.layoutManager}")
    }

    private fun setupImageMedicalReportRecyclerView() {
        // 2. Pass 'this' as the listener when creating the adapter
        medicalReportImageAdapter = MedicalReportImageAdapter(selectedImageUris, this)
        recyclerViewImageMedicalReport.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = medicalReportImageAdapter
        }
        println("Image Medical Report RecyclerView setup complete.")
    }


    private fun onPetSelected(pet: PetEntity) {
        selectedPet = pet
        Toast.makeText(
            requireContext(),
            "Selected pet: ${pet.name}",
            Toast.LENGTH_SHORT
        ).show()
        println("Selected pet ID: ${pet.id}, Name: ${pet.name}")
    }

    private fun loadPets() {
        val userId = sharedPreferences.getString(KEY_USER_ID, null)
        if (userId != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val pets = petViewModel.getPetsForHome(userId)
                    println("Loaded pets for GPS device: ${pets.size} pets")
                    selectPetAdapter.submitList(pets)

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
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. Implement the interface method
    override fun onImageLongClicked(imageUri: Uri) {
        showImageZoomDialog(imageUri)
    }

    // 4. Method to show the zoom dialog
    private fun showImageZoomDialog(imageUri: Uri) {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        // Hoặc sử dụng Dialog thông thường nếu muốn tùy chỉnh kích thước:
        // val dialog = Dialog(requireContext())
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // Ẩn title nếu là custom dialog

        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_image_zoom, null)
        val zoomedImageView = dialogView.findViewById<ImageView>(R.id.zoomedImageView)

        Glide.with(this) // Sử dụng context của Fragment
            .load(imageUri)
            .placeholder(R.drawable.ic_launcher_background) // Thay thế bằng placeholder của bạn
            .error(R.drawable.pet) // Thay thế bằng error drawable của bạn
            .into(zoomedImageView)

        dialog.setContentView(dialogView)

        // Cho phép đóng dialog khi click vào ảnh hoặc nhấn nút back
        dialogView.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                dialog.dismiss()
                return@setOnKeyListener true
            }
            false
        }
        dialog.setCancelable(true) // Cho phép đóng bằng nút back hoặc chạm ra ngoài (nếu không phải fullscreen)

        dialog.show()
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MedicalReportDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}