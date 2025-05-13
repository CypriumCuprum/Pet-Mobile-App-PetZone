package com.example.petapp.viewmodel.shop

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petapp.data.api.RetrofitClient
import com.example.petapp.data.model.CartItemCreateEntity
import com.example.petapp.data.model.ItemTypeEntity
import kotlinx.coroutines.launch

class ItemTypeViewModel : ViewModel() {
    private val _itemTypes = MutableLiveData<List<ItemTypeEntity>>()
    val itemTypes: LiveData<List<ItemTypeEntity>> get() = _itemTypes

    fun fetchItemTypes() {
        viewModelScope.launch {
            try {
                val result = RetrofitClient.apiService.getItemTypes()
                _itemTypes.value = result
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.message}")
                Log.e("API_ERROR", "Exception: ${Log.getStackTraceString(e)}")
            }
        }
    }

    fun addToCart(cartItemCreateEntity: CartItemCreateEntity){
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.addToCart(cartItemCreateEntity)
                if (response.isSuccessful) {
                    Log.d("AddToCart", "Success: ${response.body()}")
                } else {
                    Log.e("AddToCart", "Failed with status: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.message}")
                Log.e("API_ERROR", "Exception: ${Log.getStackTraceString(e)}")
            }
        }
    }
}
