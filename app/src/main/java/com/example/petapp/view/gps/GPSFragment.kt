package com.example.petapp.view.gps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.petapp.R
import com.example.petapp.data.model.GPSEntity
import com.example.petapp.data.model.PetEntity
import com.example.petapp.viewmodel.GPSDeviceViewModel
import com.example.petapp.viewmodel.pet.PetViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.random.Random

private const val PREFS_NAME = "PetAppPrefs"
private const val KEY_USER_ID = "logged_in_user_id"

class GPSFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // View components
    private lateinit var petInfoCard: CardView
    private lateinit var petDetailIcon: ImageView
    private lateinit var petNameTextView: TextView
    private lateinit var locationPinIcon: ImageView
    private lateinit var distanceTextView: TextView
    private lateinit var batteryIcon: ImageView
    private lateinit var batteryTextView: TextView
    private lateinit var btnGPSDevice: Button

    // Data structures
    private var gpsDevices = listOf<GPSEntity>()
    private var pets = listOf<PetEntity>()
    private val petMarkers = mutableMapOf<String, Marker>()
    private var petToGpsMap = mutableMapOf<String, GPSEntity>()

    // ViewModels
    private lateinit var petViewModel: PetViewModel
    private lateinit var gpsDeviceViewModel: GPSDeviceViewModel

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var map: GoogleMap
    private var selectedMarker: Marker? = null

    // Location services
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    // User's current location
    private var userLocation = LatLng(21.0378, 105.7839) // Default value

    // Location permission request
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            // Permission granted, start location updates
            startLocationUpdates()
        } else {
            // Permission denied, show notification
            Toast.makeText(
                requireContext(),
                "Location permissions are required to show your current location",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewModels
        petViewModel = ViewModelProvider(
            this,
            PetViewModel.Factory(requireActivity().application)
        )[PetViewModel::class.java]

        gpsDeviceViewModel = ViewModelProvider(
            this,
            GPSDeviceViewModel.Factory(requireActivity().application)
        )[GPSDeviceViewModel::class.java]

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_g_p_s, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Map views
        petInfoCard = view.findViewById(R.id.petInfoCard)
        petDetailIcon = view.findViewById(R.id.petDetailIcon)
        petNameTextView = view.findViewById(R.id.petNameTextView)
        locationPinIcon = view.findViewById(R.id.locationPinIcon)
        distanceTextView = view.findViewById(R.id.distanceTextView)
        batteryIcon = view.findViewById(R.id.batteryIcon)
        batteryTextView = view.findViewById(R.id.batteryTextView)
        btnGPSDevice = view.findViewById(R.id.gpsDeviceButton)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Configure location request
        createLocationRequest()

        // Initialize location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    updateUserLocation(LatLng(location.latitude, location.longitude))
                }
            }
        }

        // Get SupportMapFragment and register callback
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Hide CardView initially
        petInfoCard.visibility = View.GONE

        // Set up button click listeners
        buttonOnClick()

        // Load pet and GPS device data
        loadData()

        // Check and request location permissions
        checkLocationPermissions()
    }

    private fun loadData() {
        lifecycleScope.launch {
            val userId = sharedPreferences.getString(KEY_USER_ID, null)
            if (userId != null) {
                // Load pets for the current user
                pets = petViewModel.getPetsForHome(userId)

                // Load GPS devices for each pet
                val gpsDevicesList = mutableListOf<GPSEntity>()
                for (pet in pets) {
                    val devices = gpsDeviceViewModel.getGPSDevicesForPet(pet.id)
                    gpsDevicesList.addAll(devices)

                    // Map pet to its GPS device (assuming one pet has only one GPS device)
                    if (devices.isNotEmpty()) {
                        petToGpsMap[pet.id] = devices.first()
                    }
                }
                gpsDevices = gpsDevicesList

                // If map is ready, update markers
                if (::map.isInitialized) {
                    updateMapMarkers()
                }
            }
        }
    }

    private fun checkLocationPermissions() {
        when {
            hasLocationPermissions() -> {
                // Permissions already granted, start location updates
                startLocationUpdates()
            }

            else -> {
                // Request location permissions
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (hasLocationPermissions()) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            // Also get current location immediately
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    updateUserLocation(LatLng(it.latitude, it.longitude))
                }
            }
        }
    }

    private fun updateUserLocation(location: LatLng) {
        userLocation = location

        // Update markers on the map if map is ready
        if (::map.isInitialized) {
            updateMapMarkers()

            // Move camera to current location
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16f))

            // Update distance information for all pets
            updatePetsDistance()
        }
    }

    private fun updateMapMarkers() {
        map.clear() // Clear all old markers
        petMarkers.clear() // Clear marker reference

        // Add user location marker
        map.addMarker(
            MarkerOptions()
                .position(userLocation)
                .title("Your Location")
                .icon(bitmapDescriptorFromVector(R.drawable.your_location))
        )

        // Add pet markers with actual GPS location data
        for (pet in pets) {
            val gpsDevice = petToGpsMap[pet.id] ?: continue

            // Skip if GPS device has invalid coordinates
            if (gpsDevice.latitude < 0 || gpsDevice.longitude < 0) continue

            val petLocation = LatLng(gpsDevice.latitude, gpsDevice.longitude)

            // Create a marker for this pet
            val marker = map.addMarker(
                MarkerOptions()
                    .position(petLocation)
                    .title(pet.name)
                    .icon(createPetMarkerIcon(requireContext(), pet.name, R.drawable.ic_pet_paw))
            )

            marker?.let {
                // Store pet ID as marker tag
                it.tag = pet.id
                petMarkers[pet.id] = it
            }
        }

        // Restore selected marker if applicable
        selectedMarker?.let { marker ->
            val petId = marker.tag as? String
            petId?.let { id ->
                petMarkers[id]?.let { newMarker ->
                    selectedMarker = newMarker
                    // Find the pet and show its info card
                    pets.find { it.id == id }?.let { pet ->
                        petToGpsMap[id]?.let { gps ->
                            showPetInfoCard(pet, gps)
                        }
                    }
                }
            }
        }
    }

    private fun updatePetsDistance() {
        // Update selected marker's info if one exists
        selectedMarker?.let { marker ->
            val petId = marker.tag as? String
            if (petId != null) {
                pets.find { it.id == petId }?.let { pet ->
                    petToGpsMap[petId]?.let { gps ->
                        showPetInfoCard(pet, gps)
                    }
                }
            }
        }
    }

    private fun getBitmapDescriptorFromImageUrl(imageUrl: String): BitmapDescriptor {
        // Sử dụng bitmap mặc định trong trường hợp lỗi
        val defaultMarker = BitmapDescriptorFactory.defaultMarker()

        // Kiểm tra nếu URL rỗng
        if (imageUrl.isBlank()) {
            return defaultMarker
        }

        try {
            // Sử dụng Glide để tải ảnh từ URL và làm cho ảnh hình tròn
            val bitmap = Glide.with(requireContext())
                .asBitmap()
                .load(imageUrl)
                .apply(RequestOptions().centerCrop())
                .placeholder(R.drawable.calendar)
                .error(R.drawable.calendar)
                .submit(50, 50)  // Kích thước phù hợp cho marker
                .get()  // Lưu ý: đây là blocking call, nên hãy cẩn thận khi sử dụng

            return BitmapDescriptorFactory.fromBitmap(bitmap)
        } catch (e: Exception) {
            // Log lỗi nếu cần
            // Log.e("GPSFragment", "Error loading image from URL: $imageUrl", e)

            // Fallback to a default icon from resources
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.calendar)
            drawable?.let {
                val bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                return BitmapDescriptorFactory.fromBitmap(bitmap)
            }

            return defaultMarker
        }
    }

    @SuppressLint("CommitTransaction")
    private fun buttonOnClick() {
        btnGPSDevice.setOnClickListener {
            val gpsDeviceFragment = GPSDeviceFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, gpsDeviceFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        updateMapMarkers()
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16f))
        map.setOnMarkerClickListener(this)
        map.setOnMapClickListener {
            hidePetInfoCard()
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val petId = marker.tag as? String ?: return false

        // Find pet and GPS device data
        val pet = pets.find { it.id == petId } ?: return false
        val gpsDevice = petToGpsMap[petId] ?: return false

        selectedMarker = marker
        showPetInfoCard(pet, gpsDevice)
        return true
    }

    private fun createPetMarkerIcon(
        context: Context,
        // imageUrl: String, // No longer needed for the icon itself, can be removed or kept if used elsewhere
        petName: String,
        vectorDrawableResId: Int // Pass the resource ID of your vector drawable, e.g., R.drawable.ic_custom_marker_pin
    ): BitmapDescriptor {
        val imageSize = 60 // Diameter of the circular icon in pixels
        val padding = 8     // Padding around elements in pixels
        val textSizePx = 28f // Text size for pet name in pixels

        // 1. Create pet icon from vector drawable with random color
        var petIconBitmap: Bitmap
        try {
            val vectorDrawable: Drawable? = ContextCompat.getDrawable(context, vectorDrawableResId)
            if (vectorDrawable != null) {
                petIconBitmap = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888)
                val canvasForIcon = Canvas(petIconBitmap)

                // Generate random color
                // Using Kotlin's Random for cleaner syntax
                val r = Random.nextInt(256)
                val g = Random.nextInt(256)
                val b = Random.nextInt(256)
                val randomColor = Color.rgb(r, g, b)

                // Tint the drawable
                // Important: Use mutate() to ensure that this specific instance of the drawable is tinted,
                // and not all instances if it's cached by the system.
                val wrappedDrawable = DrawableCompat.wrap(vectorDrawable.mutate())
                DrawableCompat.setTint(wrappedDrawable, randomColor)
                wrappedDrawable.setBounds(0, 0, imageSize, imageSize)
                wrappedDrawable.draw(canvasForIcon)
            } else {
                // This case should ideally not happen if the resource ID is valid
                // Log.e("MarkerIcon", "Vector drawable with ID $vectorDrawableResId not found.")
                throw IllegalStateException("Vector drawable not found. Ensure R.drawable.your_vector_id is correct.")
            }
        } catch (e: Exception) {
            // Log.e("MarkerIcon", "Error creating vector icon, using fallback.", e)
            // Ultimate fallback: create a simple colored circle if vector loading/tinting fails
            petIconBitmap = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(petIconBitmap)
            val paint = Paint().apply { color = Color.LTGRAY; style = Paint.Style.FILL }
            canvas.drawCircle(imageSize / 2f, imageSize / 2f, imageSize / 2f, paint)
        }

        // 2. Prepare Paint for drawing text
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            this.textSize = textSizePx
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        // 3. Measure text to determine dimensions
        val textBounds = Rect()
        var nameToDraw = petName
        val maxTextWidthForIcon = imageSize * 1.5f // Allow text to be a bit wider than image
        if (textPaint.measureText(nameToDraw) > maxTextWidthForIcon) {
            while (textPaint.measureText("$nameToDraw...") > maxTextWidthForIcon && nameToDraw.length > 1) {
                nameToDraw = nameToDraw.substring(0, nameToDraw.length - 1)
            }
            nameToDraw = "$nameToDraw..."
        }
        textPaint.getTextBounds(nameToDraw, 0, nameToDraw.length, textBounds)
        val textWidth = textPaint.measureText(nameToDraw)
        val textHeight = textBounds.height() // Height of the text bounding box

        // 4. Calculate final bitmap dimensions
        val finalBitmapWidth = max(imageSize.toFloat(), textWidth).toInt() + (2 * padding)
        val finalBitmapHeight =
            imageSize.plus(textHeight) + (3 * padding) // top_pad + img_text_pad + bottom_pad

        // 5. Create final bitmap and canvas
        val finalBitmap =
            Bitmap.createBitmap(finalBitmapWidth, finalBitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(finalBitmap)

        // Optional: Draw a background/border for the entire marker
        // val backgroundPaint = Paint().apply { color = Color.argb(150, 255, 255, 255); style = Paint.Style.FILL }
        // canvas.drawRoundRect(0f, 0f, finalBitmapWidth.toFloat(), finalBitmapHeight.toFloat(), 15f, 15f, backgroundPaint)

        // 6. Draw pet icon (centered horizontally, at the top with padding)
        val imageX = (finalBitmapWidth - imageSize) / 2f
        val imageY = padding.toFloat()
        canvas.drawBitmap(petIconBitmap, imageX, imageY, null)

        // 7. Draw pet name (centered horizontally, below the icon)
        val textDrawX = finalBitmapWidth / 2f
        val textVisualTopY = imageY + imageSize + padding.toFloat()
        val textBaselineY =
            textVisualTopY - textBounds.top // Calculate baseline for correct vertical positioning

        canvas.drawText(nameToDraw, textDrawX, textBaselineY, textPaint)

        return BitmapDescriptorFactory.fromBitmap(finalBitmap)
    }

    @SuppressLint("SetTextI18n")
    private fun showPetInfoCard(pet: PetEntity, gpsDevice: GPSEntity) {
        // Calculate distance between user and pet
        val petLocation = LatLng(gpsDevice.latitude, gpsDevice.longitude)
        val distanceResults = FloatArray(1)
        Location.distanceBetween(
            userLocation.latitude, userLocation.longitude,
            petLocation.latitude, petLocation.longitude,
            distanceResults
        )
        val distance = "${distanceResults[0].toInt()}m away from you"

        // Update UI with pet info
        petNameTextView.text = pet.name
        distanceTextView.text = distance
        batteryTextView.text = gpsDevice.battery.toString() + "%"

        // Load pet image into the detail icon
        Glide.with(requireContext())
            .load(pet.imageUrl)
            .apply(RequestOptions().centerCrop())
            .placeholder(R.drawable.calendar)
            .error(R.drawable.calendar)
            .into(petDetailIcon)

        // Set other icons
        locationPinIcon.setImageResource(R.drawable.accessories1)
        batteryIcon.setImageResource(R.drawable.chart)

        petInfoCard.visibility = View.VISIBLE
    }

    private fun hidePetInfoCard() {
        petInfoCard.visibility = View.GONE
        selectedMarker = null
    }

    private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(requireContext(), vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasLocationPermissions()) {
            startLocationUpdates()
        }

        // Reload data in case it changed
        loadData()
    }

    override fun onPause() {
        super.onPause()
        // Stop location updates when Fragment is not visible
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            GPSFragment().apply {
                arguments = Bundle().apply { }
            }
    }
}