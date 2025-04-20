package com.example.petapp.view.pet

import android.app.DatePickerDialog
import android.content.Context // Import Context
import android.content.SharedPreferences // Import SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log // Import Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer // Import Observer
import androidx.lifecycle.ViewModelProvider // Import ViewModelProvider
import com.example.petapp.R
import com.example.petapp.data.local.AppDatabase // Assuming you have this
import com.example.petapp.data.model.PetEntity
import com.example.petapp.data.repository.PetRepository
// Import your ViewModel Factory if you have one
import com.example.petapp.viewmodel.pet.PetAddingViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

// Define constants for SharedPreferences
private const val PREFS_NAME = "PetAppPrefs"
private const val KEY_USER_ID = "logged_in_user_id"

class AddPetActivity : AppCompatActivity() {

    private lateinit var etPetName: EditText
    private lateinit var etBreedName: EditText
    private lateinit var etGender: EditText
    private lateinit var tvBornDate: TextView
    private lateinit var etColor: EditText
    private lateinit var etWeight: EditText
    private lateinit var etHeight: EditText
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var cardViewAvatar: CardView
    private lateinit var imageViewAvatar: ImageView
    private lateinit var linearLayoutAvatar: LinearLayout

    private var selectedDate: Calendar? = null
    private val dateFormat =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Use ISO-like format for storage

    // For avatar image
    private var selectedImageUri: Uri? = null
    private var savedImagePath: String? = null

    // --- ViewModel Setup ---
    private val petAddingViewModel: PetAddingViewModel by viewModels {
        PetAddingViewModel.Factory(
            PetRepository(
                AppDatabase.getInstance(this).petDao()
            )
        )
    }

    // --- SharedPreferences ---
    private lateinit var sharedPreferences: SharedPreferences

    // ActivityResultLauncher for image picking
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                try {
                    // Display selected image in the avatar view
                    imageViewAvatar.setImageURI(selectedImageUri)

                    // Hide the text prompts when image is selected
                    linearLayoutAvatar.visibility = View.GONE

                    // Set the imageView to be visible
                    imageViewAvatar.visibility = View.VISIBLE
                } catch (e: Exception) {
                    Log.e("AddPetActivity", "Error displaying image: ${e.message}")
                    Toast.makeText(this, "Error displaying image", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_pet)
        setupWindowInsets()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        bindViews()
        setupListeners()
        observeViewModel() // Start observing ViewModel
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun bindViews() {
        etPetName = findViewById(R.id.editTextPetName)
        etBreedName = findViewById(R.id.editTextBreedName)
        etGender = findViewById(R.id.editTextGenderName)
        tvBornDate = findViewById(R.id.textViewBornDate)
        etColor = findViewById(R.id.editTextColorName)
        etWeight = findViewById(R.id.editTextWeightName)
        etHeight = findViewById(R.id.editTextHeightName)
        btnSave = findViewById(R.id.buttonSavePet)
        progressBar = findViewById(R.id.addPetProgressBar)
        cardViewAvatar = findViewById(R.id.cardViewAvatar)
        imageViewAvatar = findViewById(R.id.imageViewAvatar)
        linearLayoutAvatar = findViewById(R.id.linearLayoutAvatar)
    }

    private fun setupListeners() {
        tvBornDate.setOnClickListener {
            showDatePickerDialog()
        }

        btnSave.setOnClickListener {
            savePetData()
        }

        cardViewAvatar.setOnClickListener {
            Toast.makeText(this, "Avatar upload clicked", Toast.LENGTH_SHORT).show()
            setPetAvatar()
        }
    }

    private fun setPetAvatar() {
        // Launch gallery to pick an image
        pickImage.launch("image/*")
    }

    private fun saveImageToInternalStorage(): String {
        if (selectedImageUri == null) {
            println("No image selected")
            return ""
        }

        try {
            // Generate a unique filename using UUID
            val filename = "pet_avatar_${UUID.randomUUID()}.jpg"
            println("Generated filename: $filename")
            val file = File(filesDir, filename)

            // Create an input stream from the selected URI
            val inputStream = contentResolver.openInputStream(selectedImageUri!!)

            // Create a bitmap from the input stream
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Save the bitmap to the app's internal storage
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                outputStream.flush()
            }

            Log.d("AddPetActivity", "Image saved successfully at: ${file.absolutePath}")
            return file.absolutePath

        } catch (e: IOException) {
            Log.e("AddPetActivity", "Error saving image: ${e.message}")
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
            return ""
        }
    }

    private fun observeViewModel() {
        petAddingViewModel.addPetResult.observe(this, Observer { petEntity ->
            progressBar.visibility = View.GONE
            btnSave.isEnabled = true
            if (petEntity != null) {
                // Success
                Toast.makeText(
                    this,
                    "Pet '${petEntity.name}' saved successfully!",
                    Toast.LENGTH_LONG
                ).show()
                Log.i(
                    "AddPetActivity",
                    "Pet saved with ID: ${petEntity.id} for User ID: ${petEntity.userId}"
                )
                finish() // Close activity on success
            }
            // Failure case is handled by addPetError observer
        })

        petAddingViewModel.addPetError.observe(this, Observer { errorMessage ->
            if (errorMessage != null) {
                // Error occurred
                progressBar.visibility = View.GONE
                btnSave.isEnabled = true
                Toast.makeText(this, "Error saving pet: $errorMessage", Toast.LENGTH_LONG).show()
                Log.e("AddPetActivity", "Error saving pet: $errorMessage")
            }
        })
    }


    private fun showDatePickerDialog() {
        val calendar = selectedDate ?: Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val newSelectedDate = Calendar.getInstance()
                newSelectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)
                selectedDate = newSelectedDate
                // Display format can be different from storage format
                val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                tvBornDate.text = displayFormat.format(selectedDate!!.time)
                // tvBornDate.setTextColor(getColor(R.color.black)) // Example: change color back
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun savePetData() {
        // --- Get User ID ---
        val userId = sharedPreferences.getString(KEY_USER_ID, null)

        if (userId == null) {
            Toast.makeText(this, "Error: User not logged in. Cannot save pet.", Toast.LENGTH_LONG)
                .show()
            Log.e(
                "AddPetActivity",
                "User ID not found in SharedPreferences ($PREFS_NAME, $KEY_USER_ID)"
            )
            return
        }

        Log.d("AddPetActivity", "Retrieved User ID: $userId")

        // --- Validation ---
        val petName = etPetName.text.toString().trim()
        val breedName = etBreedName.text.toString().trim()
        val gender = etGender.text.toString().trim()
        val color = etColor.text.toString().trim()
        val heightStr = etHeight.text.toString().trim()
        val weightStr = etWeight.text.toString().trim()

        // Basic Input Validation
        if (petName.isEmpty()) {
            etPetName.error = "Pet name is required"
            etPetName.requestFocus()
            return
        }
        if (breedName.isEmpty()) {
            etBreedName.error = "Breed name is required"
            etBreedName.requestFocus()
            return
        }
        if (gender.isEmpty()) {
            etGender.error = "Gender is required"
            etGender.requestFocus()
            return
        }
        if (selectedDate == null) {
            Toast.makeText(this, R.string.add_pet_date_required, Toast.LENGTH_LONG).show()
            return
        }
        val height = heightStr.toFloatOrNull()
        if (height == null || height <= 0) {
            etHeight.error = "Valid height required (must be > 0)"
            etHeight.requestFocus()
            return
        }
        val weight = weightStr.toFloatOrNull()
        if (weight == null || weight <= 0) {
            etWeight.error = "Valid weight required (must be > 0)"
            etWeight.requestFocus()
            return
        }

        // --- If validation passes ---
        progressBar.visibility = View.VISIBLE
        btnSave.isEnabled = false

        // Format date for storage (e.g., YYYY-MM-DD)
        val formattedBirthDate = dateFormat.format(selectedDate!!.time)

        // Save image if one was selected
        var imageUrl: String = "ok"
        if (selectedImageUri != null) {
            imageUrl = saveImageToInternalStorage()
            println("Saved image path: $imageUrl")
        }

        // Create PetEntity object
        val newPet = PetEntity(
            id = UUID.randomUUID().toString(), // Generate new UUID
            name = petName,
            breedName = breedName,
            gender = gender,
            birthDate = formattedBirthDate,
            color = color.ifEmpty { null }, // Store null if empty
            height = height,
            weight = weight,
            imageUrl = imageUrl,
            note = null, // Add an EditText for notes if needed
            userId = userId, // Use the retrieved user ID
            // createdAt, updatedAt, isSynced, lastSync will use defaults or be set by Room/Repo
        )

        // --- Call ViewModel to add the pet ---
        Log.d("AddPetActivity", "Calling ViewModel to add pet: $newPet")
        petAddingViewModel.addPet(newPet)
    }
}