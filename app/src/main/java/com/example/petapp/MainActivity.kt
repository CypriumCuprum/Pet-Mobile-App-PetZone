package com.example.petapp

import android.graphics.Color
import android.os.Build // Giữ lại nếu cần
import android.os.Bundle
import android.view.View
import android.widget.TextView // Cần thiết để tìm TextView bên trong Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi // Giữ lại nếu cần
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.petapp.view.HomeFragment
import com.example.petapp.view.petHealth.PetHealth
import com.example.petapp.view.reminder.Reminder1Fragment
// import com.example.petapp.view.reminder.Add_reminderFragment
import com.example.petapp.view.shop.Shop
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbarView: View // Biến cho View của Toolbar được include

    // private lateinit var toolbarTitle: TextView // Không cần tham chiếu trực tiếp ở cấp lớp nữa
    private var lastSelectedItem: View? = null

    // @RequiresApi(Build.VERSION_CODES.R) // Xóa nếu không cần
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Khởi tạo các view
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        toolbarView = findViewById(R.id.toolbar) // Lấy tham chiếu đến View của Toolbar đã include

        // Làm status bar trong suốt
        window.statusBarColor = Color.TRANSPARENT

        // Xử lý insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, systemBars.bottom)
            insets
        }

        // Thiết lập listener cho bottom navigation
        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment
            var hideToolbar = false
            var title = "" // Lưu title để đặt khi toolbar hiển thị

            when (item.itemId) {
                R.id.nav_home -> {
                    selectedFragment = HomeFragment()
                    // title = "Home" // Không cần title khi toolbar ẩn
                    hideToolbar = true
                }

                R.id.nav_community -> {
                    selectedFragment = Reminder1Fragment()
                    title = "Reminder"
                }

                R.id.nav_shop -> {
                    selectedFragment = Shop()
                    title = "Shop"
                }

                R.id.nav_profile -> {
                    selectedFragment = PetHealth()
                    title = "Profile" // Hoặc "Pet Health"
                }

                else -> {
                    selectedFragment = HomeFragment()
                    // title = "Home"
                    hideToolbar = true
                }
            }

            // --- Logic ẩn/hiện TOÀN BỘ Toolbar ---
            if (hideToolbar) {
                toolbarView.visibility = View.GONE // Ẩn toàn bộ Toolbar
            } else {
                toolbarView.visibility = View.VISIBLE // Hiển thị Toolbar
                // Tìm TextView bên trong Toolbar và đặt tiêu đề
                // **Quan trọng:** Đảm bảo ID `toolbar_title` tồn tại trong layout `toolbar.xml` của bạn
                val toolbarTitleTextView = toolbarView.findViewById<TextView>(R.id.toolbar_title)
                toolbarTitleTextView?.text = title
            }
            // --------------------------------------

            // Đặt hiệu ứng nền cho item được chọn
            val itemView = bottomNavigationView.findViewById<BottomNavigationItemView>(item.itemId)
            lastSelectedItem?.background = null
            // Cẩn thận null check nếu itemView có thể không tìm thấy (dù hiếm)
            itemView?.setBackgroundResource(R.drawable.nav_selected_menu)
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