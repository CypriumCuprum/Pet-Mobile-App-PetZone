package com.example.petapp.view.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petapp.MainActivity
import com.example.petapp.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button_login = findViewById<Button>(R.id.buttonLogin)
        button_login.setOnClickListener {
            val intentmain = Intent(this, DoingLoginActivity::class.java)
            startActivity(intentmain)
        }

        //Test o day
        val button_dev = findViewById<Button>(R.id.buttonRegister)
        button_dev.setOnClickListener {
            val intentmain = Intent(this, MainActivity::class.java)
            startActivity(intentmain)
        }
    }
}