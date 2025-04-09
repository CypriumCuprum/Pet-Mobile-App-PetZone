package com.example.petapp.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petapp.data.model.UserEntity
import com.example.petapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class SignUpViewModel(private val repository: UserRepository) : ViewModel() {
    private val _signUpResult = MutableLiveData<Boolean>(false)
    val signUpResult: MutableLiveData<Boolean> get() = _signUpResult

    // LiveData cho trạng thái đang tải
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData cho thông báo lỗi
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun registerUser(
        yourname: String,
        username: String,
        passwordInput: String,
        confirmPasswordInput: String
    ) {
        // 1. Validate Input (Client-side basic validation)
        if (yourname.isBlank() || username.isBlank() || passwordInput.isBlank() || confirmPasswordInput.isBlank()) {
            _errorMessage.value = "Vui lòng điền đầy đủ tên đăng nhập và mật khẩu."
            return
        }
        if (passwordInput != confirmPasswordInput) {
            _errorMessage.value = "Mật khẩu xác nhận không khớp."
            return
        }
        // Optional: Thêm kiểm tra độ mạnh mật khẩu ở đây

        // 2. Bắt đầu xử lý
        _isLoading.value = true
        _errorMessage.value = null // Xóa lỗi cũ

        viewModelScope.launch {
            try {
                // 3. Kiểm tra username đã tồn tại chưa
                val existingUser = repository.getUserByUsername(username)
                if (existingUser != null) {
                    _errorMessage.postValue("Tên đăng nhập đã tồn tại.")
                    _signUpResult.postValue(false) // Đăng ký thất bại
                    _isLoading.postValue(false) // Dừng loading
                    return@launch // Thoát coroutine
                }

                // --- !!! CẢNH BÁO BẢO MẬT !!! ---
                // BẠN PHẢI HASH MẬT KHẨU Ở ĐÂY TRƯỚC KHI LƯU!
                // Sử dụng thư viện như BCrypt hoặc Argon2.
                // val hashedPassword = hashPasswordFunction(passwordInput) // Hàm hash của bạn
                val hashedPassword =
                    passwordInput // <-- Chỉ dùng cho demo, THAY THẾ BẰNG HASH THỰC TẾ

                // 4. Tạo đối tượng UserEntity mới
                val newUser = UserEntity(
                    username = username,
                    password = hashedPassword, // Lưu mật khẩu đã hash
                    fullname = yourname
                )

                // 5. Gọi Repository để đăng ký (chèn vào DB)
                repository.register(newUser) // Giả sử repo có hàm này gọi DAO.insert

                // 6. Thông báo thành công
                _signUpResult.postValue(true)

            } catch (e: Exception) {
                // 7. Xử lý lỗi (Ví dụ: Lỗi DB)
                _errorMessage.postValue("Đăng ký thất bại: ${e.message}")
                _signUpResult.postValue(false)
                e.printStackTrace()
            } finally {
                // 8. Dừng trạng thái loading
                _isLoading.postValue(false)
            }
        }
    }

    fun onErrorMessageShown() {
        _errorMessage.value = null
    }
}