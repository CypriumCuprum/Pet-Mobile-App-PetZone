package com.example.petapp.viewmodel.user

import androidx.lifecycle.*
import com.example.petapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> get() = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val user = repository.login(username, password)
            _loginResult.value = user != null
        }
    }
}