package com.example.petapp

import android.graphics.Color
import android.media.Image
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.petapp.view.shop.Shop
import androidx.fragment.app.Fragment
import com.example.petapp.view.HomeFragment
import com.example.petapp.reminder.Reminder1Fragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbarTitle: TextView
    private var lastSelectedItem: View? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Khởi tạo các view
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        toolbarTitle = findViewById(R.id.toolbar_title)

        // Thiết lập để nội dung vẽ dưới status bar
        window.setDecorFitsSystemWindows(false)

        // Làm status bar trong suốt
        window.statusBarColor = Color.TRANSPARENT

        // Xử lý insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Thiết lập tiêu đề mặc định
        toolbarTitle.text = "Home"

        // Thiết lập listener cho bottom navigation
        bottomNavigationView.setOnItemSelectedListener { item ->
            // Xác định fragment và tiêu đề dựa trên item được chọn
            val selectedFragment: Fragment
            val title: String
            when (item.itemId) {
                R.id.nav_home -> {
                    selectedFragment = HomeFragment()
                    title = "Home"
                }

                R.id.nav_community -> {
                    selectedFragment = Reminder1Fragment()
                    title = "Reminder"
                }
//                R.id.nav_community -> {
//                    selectedFragment = CommunityFragment()
//                    title = "Community"
//                }
//                R.id.nav_gps -> {
//                    selectedFragment = GPSFragment()
//                    title = "GPS"
//                }
                R.id.nav_shop -> {
                    selectedFragment = Shop()
                    title = "Shop"
                }
//                R.id.nav_profile -> {
//                    selectedFragment = ProfileFragment()
//                    title = "Profile"
//                }
                else -> {
                    selectedFragment = HomeFragment()
                    title = "Home"
                }
            }

            // Cập nhật tiêu đề toolbar
            toolbarTitle.text = title

            // Đặt hiệu ứng cho item được chọn
            val itemView = bottomNavigationView.findViewById<BottomNavigationItemView>(item.itemId)

            // Reset background cho item trước đó (nếu có)
            lastSelectedItem?.background = null

            // Đặt background cho item mới được chọn
            itemView.setBackgroundResource(R.drawable.nav_selected_menu)

            // Cập nhật item được chọn
            lastSelectedItem = itemView

            // Thay thế fragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, selectedFragment)
                .commit()

            true
        }

        // Chọn item Home làm mặc định khi khởi động ứng dụng
        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.nav_home
        }
    }

}