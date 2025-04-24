package com.example.petapp.view.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.petapp.MainActivity
import com.example.petapp.R
import com.example.petapp.data.local.AppDatabase
import com.example.petapp.data.repository.UserRepository
import com.example.petapp.viewmodel.user.LoginViewModel

class DoingLoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModel.Factory(
            UserRepository(AppDatabase.getInstance(applicationContext).userDao()),
            application
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_doing_login)
        val appDatabase = AppDatabase.getInstance(this)
        // Check if user is already logged in
        if (viewModel.isUserLoggedIn()) {
            navigateToMain()
            return
        }

        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.buttonLogin)
        val registerButton = findViewById<Button>(R.id.buttonSignUp)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            viewModel.login(username, password)
        }

        viewModel.loginResult.observe(this) { loggedInUser ->
            if (loggedInUser != null) {
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loginError.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        // Observe navigation events
        viewModel.navigateToMain.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                navigateToMain()
                viewModel.navigationComplete()
            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}