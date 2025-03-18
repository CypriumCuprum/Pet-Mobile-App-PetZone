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
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView;
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Thiết lập để nội dung vẽ dưới status bar
        window.setDecorFitsSystemWindows(false)
        // Làm status bar trong suốt
        window.statusBarColor = Color.TRANSPARENT
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.setText(R.string.home)

        val toolbarRightIcon = findViewById<ImageView>(R.id.right_icon_toolbar)
        toolbarRightIcon.setOnClickListener({
            toolbarRightIcon.setImageResource(R.drawable.cart)
            val fragment = Shop()
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment) // Thay thế Fragment hiện tại
                .addToBackStack(null) // Cho phép quay lại Fragment trước đó
                .commit()
        })

    }

}