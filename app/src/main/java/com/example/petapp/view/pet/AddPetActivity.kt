package com.example.petapp.view.pet

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petapp.R

class AddPetActivity : AppCompatActivity() {

    private lateinit var etPetName: EditText
    private lateinit var etBreedName: EditText
    private lateinit var etGender: EditText
    private lateinit var etAge: EditText
    private lateinit var etColor: EditText
    private lateinit var etWeight: EditText
    private lateinit var etHeight: EditText
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar

//    private val viewModel: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_pet)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }
}