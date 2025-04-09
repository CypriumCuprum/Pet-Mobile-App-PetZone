package com.example.petapp.viewmodel.user

import androidx.lifecycle.*
import com.example.petapp.data.model.UserEntity
import com.example.petapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<UserEntity?>()
    val loginResult: LiveData<UserEntity?> get() = _loginResult

    // Bạn có thể giữ LiveData này nếu cần thông báo lỗi cụ thể hơn
    private val _loginError = MutableLiveData<String?>()
    val loginError: LiveData<String?> = _loginError

    //    fun login(username: String, password: String) {
//        viewModelScope.launch {
//            val user = repository.login(username, password)
//            _loginResult.value = user
//        }
//    }
    fun login(username: String, password: String /*plain text - không an toàn*/) {
        viewModelScope.launch {
            try {
                println("Login with username: $username and password: $password")
                val user = repository.login(username, password) // Lấy user từ repo
                // !!! Quan trọng: So sánh mật khẩu đã HASH trong thực tế !!!
                // in ra user
                println("User: $user")
                if (user != null) {
                    _loginResult.postValue(user) // Gửi cả đối tượng User khi thành công
                    _loginError.postValue(null) // Xóa lỗi cũ (nếu có)
                } else {
                    _loginResult.postValue(null) // Gửi null khi thất bại
                    _loginError.postValue("Tên đăng nhập hoặc mật khẩu không đúng.")
                }
            } catch (e: Exception) {
                _loginResult.postValue(null)
                _loginError.postValue("Đã xảy ra lỗi: ${e.message}")
            }
        }
    }
}