package com.example.petapp.view.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petapp.R
import com.example.petapp.data.local.AppDatabase
import com.example.petapp.data.repository.UserRepository
import com.example.petapp.viewmodel.user.SignUpViewModel

class RegistrationActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etYourName: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var btnLogIn: Button
    private lateinit var progressBar: ProgressBar

    private val viewModel: SignUpViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
                    val db = AppDatabase.getInstance(applicationContext)
                    val repo = UserRepository(db.userDao())
                    return SignUpViewModel(repo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnSignUp = findViewById<Button>(R.id.buttonSignUp)
        btnLogIn = findViewById<Button>(R.id.buttonLogIn)
        etUsername = findViewById<EditText>(R.id.username)
        etYourName = findViewById<EditText>(R.id.yourname)
        etPassword = findViewById<EditText>(R.id.password)
        etConfirmPassword = findViewById<EditText>(R.id.confirmpassword)
        progressBar = findViewById<ProgressBar>(R.id.registerProgressBar)

        btnSignUp.setOnClickListener() {
            handleSignUp()
        }

        btnLogIn.setOnClickListener() {
            // Handle login button click
            // back to login activity
            finish()
        }
        observeViewModel()
    }

    private fun handleSignUp() {
        val username = etUsername.text.toString()
        val yourName = etYourName.text.toString()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()
        println("Sign up with username: $username and password: $password")
        viewModel.registerUser(
            yourname = yourName,
            username = username,
            passwordInput = password,
            confirmPasswordInput = confirmPassword
        )
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                progressBar.visibility = ProgressBar.VISIBLE
            } else {
                progressBar.visibility = ProgressBar.GONE
            }
            etYourName.isEnabled = !isLoading
            etUsername.isEnabled = !isLoading
            etPassword.isEnabled = !isLoading
            etConfirmPassword.isEnabled = !isLoading
            btnSignUp.isEnabled = !isLoading
            btnLogIn.isEnabled = !isLoading
        }

        viewModel.signUpResult.observe(this) { isSuccess ->
            // Chỉ xử lý khi isSuccess là true và không đang loading (tránh xử lý lặp lại)
            // Lưu ý: Cần kiểm tra cẩn thận logic này, có thể cần cờ "đã xử lý"
            // hoặc chỉ xử lý khi giá trị thay đổi từ false sang true.
            // Cách đơn giản hơn là chỉ phản ứng khi isSuccess là true rõ ràng.
            println("Sign up result: $isSuccess")
            if (isSuccess) { // Kiểm tra isLoading để chắc chắn quá trình đã xong
                println("Đăng ký thành công!")
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                // Chuyển về màn hình đăng nhập sau khi đăng ký thành công
                finish() // Đóng RegisterActivity, quay về màn hình trước đó (thường là Login)
            }
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let { // Chỉ thực hiện nếu errorMessage không null
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                // --- GỌI HÀM ĐỂ RESET LỖI SAU KHI HIỂN THỊ ---
                viewModel.onErrorMessageShown()
                // ---------------------------------------------
            }
        }
    }
}