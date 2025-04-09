package com.example.petapp.view.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.petapp.MainActivity
import com.example.petapp.R
import com.example.petapp.data.local.AppDatabase
import com.example.petapp.data.repository.UserRepository
import com.example.petapp.viewmodel.user.LoginViewModel

class DoingLoginActivity : AppCompatActivity() {

    // --- SharedPreferences Constants ---
    companion object {
        const val PREFS_NAME = "PetAppPrefs" // Tên file preferences
        const val KEY_USER_ID = "logged_in_user_id" // Key để lưu user ID
        const val INVALID_USER_ID = -1 // Giá trị mặc định nếu chưa đăng nhập
    }

    // ---------------------------------
    private val viewModel: LoginViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDatabase.getInstance(applicationContext)
                val repo = UserRepository(db.userDao())
                return LoginViewModel(repo) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_doing_login)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.buttonLogin)
        val registerButton = findViewById<Button>(R.id.buttonSignIn)
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            println("Login with username: $username and password: $password")
            // Check login
            viewModel.login(username, password)
        }

        viewModel.loginResult.observe(this) { loggedInUser ->
            if (loggedInUser != null) {
                // Login successful!
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                // --- Lưu userId vào SharedPreferences ---
                saveLoginSession(loggedInUser.id) // Giả sử User có thuộc tính userId
                // ----------------------------------------

                // Navigate to MainActivity
                navigateToMain()

            }
            // Không cần else ở đây nếu chỉ xử lý thành công
            // Việc xử lý lỗi có thể quan sát viewModel.loginError riêng
        }

        // Quan sát lỗi (tùy chọn, để hiển thị thông báo lỗi tốt hơn)
        viewModel.loginError.observe(this) { errorMessage ->
            errorMessage?.let { // Chỉ hiển thị Toast nếu có lỗi
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                // Bạn có thể xóa lỗi trong ViewModel sau khi hiển thị để tránh hiển thị lại
                // viewModel.clearError() // Thêm hàm này vào ViewModel nếu cần
            }
        }
        // ------------------------------------------------
        registerButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

    }

    // --- Hàm lưu session ---
    private fun saveLoginSession(userId: String) {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(KEY_USER_ID, userId) // Lưu userId vào SharedPreferences
            apply() // Dùng apply() để lưu bất đồng bộ, không block UI thread
        }
    }

    // --- Hàm kiểm tra đã đăng nhập ---
    private fun isUserLoggedIn(): Boolean {
        return getLoggedInUserId() != INVALID_USER_ID
    }

    // --- Hàm lấy userId đã đăng nhập (sẽ dùng ở các màn hình khác) ---
    private fun getLoggedInUserId(): Int {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Trả về INVALID_USER_ID (-1) nếu không tìm thấy key
        return sharedPref.getInt(KEY_USER_ID, INVALID_USER_ID)
    }

    // --- Hàm điều hướng tới Main ---
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            // Xóa hết các activity trước đó khỏi stack, đảm bảo không back lại Login được
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish() // Đóng LoginActivity
    }

    // --- (Optional) Hàm đăng xuất (có thể đặt ở MainActivity hoặc Profile) ---
    /*
    private fun clearLoginSession() {
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            remove(KEY_USER_ID) // Xóa key user id
            // hoặc dùng clear() nếu muốn xóa hết SharedPreferences của app
            apply()
        }
        // Sau đó điều hướng về LoginActivity
        // val intent = Intent(this, DoingLoginActivity::class.java).apply {
        //     flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        // }
        // startActivity(intent)
        // finish()
    }
    */
}
