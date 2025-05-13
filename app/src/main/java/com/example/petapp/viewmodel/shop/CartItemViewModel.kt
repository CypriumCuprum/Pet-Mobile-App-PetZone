package com.example.petapp.viewmodel.shop

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petapp.data.api.RetrofitClient
import com.example.petapp.data.model.CartEntity
import com.example.petapp.data.model.CartItemCreateEntity
import com.example.petapp.data.model.CartItemEntity
import com.example.petapp.data.model.OrderCreate
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID

class CartItemViewModel : ViewModel() {
    private val _cartData = MutableLiveData<CartEntity>()
    val cartData: LiveData<CartEntity> = _cartData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun addToCart(cartItemCreateEntity: CartItemCreateEntity, onResult: (Boolean, String) -> Unit){
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.addToCart(cartItemCreateEntity)
                if (response.isSuccessful) {
                    onResult(true, "Item was successfully added to your cart.")
                    Log.d("AddToCart", "Success: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = JSONObject(errorBody ?: "{}").optString("detail", "Unknown error")
                    onResult(false, "Failed to add item to cart: $errorMessage")
                    Log.e("AddToCart", "Failed with status: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onResult(false, "Network or server error: ${e.message}")
                Log.e("API", "Error: ${e.message}")
                Log.e("API_ERROR", "Exception: ${Log.getStackTraceString(e)}")
            }
        }
    }

    fun loadUserCart(userId: UUID){
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getCartByUserId((userId))
                if (response.isNotEmpty()) {
                    _cartData.value = response[0] // Taking first cart for the user
                } else {
                    _errorMessage.value = "Cart is empty"
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.message}")
                Log.e("API_ERROR", "Exception: ${Log.getStackTraceString(e)}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun increaseQuantity(cartItem: CartItemEntity) {
        viewModelScope.launch {
            try {
                // In a real app, you would call an API to update the cart
                val currentCart = _cartData.value ?: return@launch

                // Create an updated cart with the increased quantity
                val updatedItems = currentCart.listItem.map {
                    if (it.id == cartItem.id) {
                        it.copy(quantity = it.quantity + 1)
                    } else {
                        it
                    }
                }

                // Update the LiveData with the new cart
                _cartData.value = currentCart.copy(listItem = updatedItems)

                // In a real app, you would also sync this with the server
                // repository.updateCartItem(cartItem.id, cartItem.quantity + 1)
            } catch (e: Exception) {
                _errorMessage.value = "Error updating quantity: ${e.message}"
            }
        }
    }

    fun decreaseQuantity(cartItem: CartItemEntity) {
        if (cartItem.quantity <= 1) return

        viewModelScope.launch {
            try {
                val currentCart = _cartData.value ?: return@launch

                // Create an updated cart with the decreased quantity
                val updatedItems = currentCart.listItem.map {
                    if (it.id == cartItem.id) {
                        it.copy(quantity = it.quantity - 1)
                    } else {
                        it
                    }
                }

                // Update the LiveData with the new cart
                _cartData.value = currentCart.copy(listItem = updatedItems)

                // In a real app, you would sync with the server
                // repository.updateCartItem(cartItem.id, cartItem.quantity - 1)
            } catch (e: Exception) {
                _errorMessage.value = "Error updating quantity: ${e.message}"
            }
        }
    }

    fun deleteCartItem(cartItemId: UUID){
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.deleteCartItem(cartItemId)
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.message}")
                Log.e("API_ERROR", "Exception: ${Log.getStackTraceString(e)}")
            }
        }
    }

    fun removeCartItem(cartItemId: UUID){
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.removeCartItem(cartItemId)
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.message}")
                Log.e("API_ERROR", "Exception: ${Log.getStackTraceString(e)}")
            }
        }
    }

    fun createOrder(orderCreate: OrderCreate, onResult: (Boolean, String) -> Unit){
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.createOrder(orderCreate)
                if (response.isSuccessful) {
                    onResult(true, "Your order has been placed successfully.")
                    Log.d("CreateOrder", "Success: ${response.body()}")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    onResult(false, "Failed to place order: $errorMessage")
                    Log.e("CreateOrder", "Failed with status: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onResult(false, "Network or server error: ${e.message}")
                Log.e("API", "Error: ${e.message}")
                Log.e("API_ERROR", "Exception: ${Log.getStackTraceString(e)}")
            }
        }
    }

    private fun getUserId(): String {
        // In a real app, get this from SharedPreferences or authentication service
        return "3d8f1550-0abb-11f0-992e-0250e6cba39f"
    }
}
