package com.example.petapp.viewmodel.user

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.petapp.data.model.UserEntity
import com.example.petapp.data.repository.UserRepository
import kotlinx.coroutines.launch
import android.util.Log
import com.example.petapp.data.local.AppDatabase
import com.example.petapp.data.repository.PetRepository

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: UserRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = UserRepository(db.userDao())
    }

    // SharedPreferences Constants
    companion object {
        const val PREFS_NAME = "PetAppPrefs"
        const val KEY_USER_ID = "logged_in_user_id"
        const val INVALID_USER_ID = -1
    }

    private val _loginResult = MutableLiveData<UserEntity?>()
    val loginResult: LiveData<UserEntity?> get() = _loginResult

    private val _loginError = MutableLiveData<String?>()
    val loginError: LiveData<String?> = _loginError

    // LiveData to notify Activity when to navigate
    private val _navigateToMain = MutableLiveData<Boolean>()
    val navigateToMain: LiveData<Boolean> get() = _navigateToMain

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                println("Login with username: $username and password: $password")
                val user = repository.login(username, password)
                println("User: $user")

                if (user != null) {
                    // Save login session when successful
                    saveLoginSession(user.id)
                    _loginResult.postValue(user)
                    _loginError.postValue(null)
                    // Trigger navigation
                    _navigateToMain.postValue(true)
                } else {
                    _loginResult.postValue(null)
                    _loginError.postValue("Tên đăng nhập hoặc mật khẩu không đúng.")
                }
            } catch (e: Exception) {
                _loginResult.postValue(null)
                _loginError.postValue("Đã xảy ra lỗi: ${e.message}")
            }
        }
    }

    // SharedPreferences methods moved from Activity
    private fun saveLoginSession(userId: String) {
        val context = getApplication<Application>().applicationContext
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(KEY_USER_ID, userId)
            apply()
        }
    }

    // --- MODIFIED: isUserLoggedIn ---
    fun isUserLoggedIn(): Boolean {
        // User is logged in if the retrieved User ID String is not null (or not empty, if you prefer)
        return getLoggedInUserId() != null
    }

    // --- MODIFIED: getLoggedInUserId ---
    fun getLoggedInUserId(): String? { // Return type changed to nullable String
        val context = getApplication<Application>().applicationContext
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Use getString, default to null if the key is not found
        val userId = sharedPref.getString(KEY_USER_ID, null)
        Log.d("LoginViewModel", "Retrieved user ID (String?): $userId") // Log retrieved value
        return userId
    }

    // Reset navigation state after navigating
    fun navigationComplete() {
        _navigateToMain.value = false
    }

    // Factory class for creating the ViewModel with Application context
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}