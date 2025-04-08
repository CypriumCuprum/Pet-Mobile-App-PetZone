package com.example.petapp.viewmodel.user

import androidx.lifecycle.*
import com.example.petapp.data.model.UserEntity
import com.example.petapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<UserEntity?>()
    val loginSuccess: LiveData<UserEntity?> get() = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val user = repository.login(username, password)
            _loginResult.value = user
        }
    }
}