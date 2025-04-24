package com.example.petapp.view.gps

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView // Import CardView
import androidx.core.content.ContextCompat
import com.example.petapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

// Xóa import ViewBinding: import com.example.petapp.databinding.FragmentGPSBinding

class GPSFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // Khai báo các View sẽ sử dụng
    private lateinit var petInfoCard: CardView
    private lateinit var petDetailIcon: ImageView
    private lateinit var petNameTextView: TextView
    private lateinit var locationPinIcon: ImageView
    private lateinit var distanceTextView: TextView
    private lateinit var batteryIcon: ImageView
    private lateinit var batteryTextView: TextView

    // Không cần khai báo Button gpsDeviceButton nếu bạn chưa xử lý sự kiện click cho nó
    private lateinit var btnGPSDevice: Button

    private lateinit var map: GoogleMap
    private var selectedMarker: Marker? = null

    // --- Dữ liệu Hardcoded (Giữ nguyên) ---
    private val userLocation = LatLng(21.0378, 105.7839)
    private val petLocationDog = LatLng(21.0385, 105.7845)
    private val petLocationCat = LatLng(21.0365, 105.7850)

    private val petDetails = mapOf(
        "DOG_POMERANIAN" to PetInfo(
            "Pomeranian",
            "200m away from you",
            70,
            R.drawable.calendar,
            R.drawable.calendar
        ),
        "CAT_TANJIRO" to PetInfo(
            "Tanjiro Kamado",
            "400m away from you",
            50,
            R.drawable.calendar,
            R.drawable.calendar
        )
    )

    private data class PetInfo(
        val name: String,
        val distance: String,
        val battery: Int,
        val detailIconRes: Int,
        val markerIconRes: Int
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { // Kiểu trả về có thể là View?
        // Inflate layout theo cách thông thường
        return inflater.inflate(R.layout.fragment_g_p_s, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Ánh xạ Views sử dụng findViewById ---
        petInfoCard = view.findViewById(R.id.petInfoCard)
        petDetailIcon = view.findViewById(R.id.petDetailIcon)
        petNameTextView = view.findViewById(R.id.petNameTextView)
        locationPinIcon = view.findViewById(R.id.locationPinIcon)
        distanceTextView = view.findViewById(R.id.distanceTextView)
        batteryIcon = view.findViewById(R.id.batteryIcon)
        batteryTextView = view.findViewById(R.id.batteryTextView)
        btnGPSDevice = view.findViewById(R.id.gpsDeviceButton)
        // Ánh xạ các view khác nếu cần

        // Lấy SupportMapFragment và đăng ký callback
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Ẩn CardView ban đầu
        petInfoCard.visibility = View.GONE

        buttonOnClick()
    }

    private fun buttonOnClick() {
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        addMarkers()
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16f))
        map.setOnMarkerClickListener(this)
        map.setOnMapClickListener {
            hidePetInfoCard()
        }
    }

    private fun addMarkers() {
        // Marker người dùng
        map.addMarker(
            MarkerOptions()
                .position(userLocation)
                .title("Your Location")
                .icon(bitmapDescriptorFromVector(R.drawable.your_location))
        )

        // Marker thú cưng (Chó)
        val dogMarker = map.addMarker(
            MarkerOptions()
                .position(petLocationDog)
                .title("Pomeranian")
                .icon(bitmapDescriptorFromVector(R.drawable.calendar))
        )
        dogMarker?.tag = "DOG_POMERANIAN"

        // Marker thú cưng (Mèo)
        val catMarker = map.addMarker(
            MarkerOptions()
                .position(petLocationCat)
                .title("Tanjiro Kamado")
                .icon(bitmapDescriptorFromVector(R.drawable.calendar))
        )
        catMarker?.tag = "CAT_TANJIRO"
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val petTag = marker.tag as? String ?: return false
        val petInfo = petDetails[petTag] ?: return false
        selectedMarker = marker
        showPetInfoCard(petInfo)
        return true
    }

    private fun showPetInfoCard(petInfo: PetInfo) {
        // Sử dụng trực tiếp các biến View đã được ánh xạ
        petNameTextView.text = petInfo.name
        distanceTextView.text = petInfo.distance
        batteryTextView.text = "${petInfo.battery}%"
        petDetailIcon.setImageResource(petInfo.detailIconRes)
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

    // Không cần override onDestroyView nếu bạn không làm gì thêm ở đây
    // override fun onDestroyView() {
    //     super.onDestroyView()
    // }

    // --- Phần Companion Object (Giữ nguyên) ---
    companion object {
        @JvmStatic
        fun newInstance() =
            GPSFragment().apply {
                arguments = Bundle().apply { }
            }
    }
}