package com.example.petapp.data.api

import com.example.petapp.data.model.CartEntity
import com.example.petapp.data.model.CartItemCreateEntity
import com.example.petapp.data.model.CartItemQuantity
import com.example.petapp.data.model.ItemTypeEntity
import com.example.petapp.data.model.OrderCreate
import com.example.petapp.data.model.OrderEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface ApiService {
    @GET("item_type/get")
    suspend fun getItemTypes(): List<ItemTypeEntity>

    @POST("cart/add_to_cart")
    suspend fun addToCart(@Body cartItemCreateEntity: CartItemCreateEntity): Response<Map<String, String>>

    @GET("cart/user/{userId}")
    suspend fun getCartByUserId(@Path("userId") userId: UUID): List<CartEntity>

    @PATCH("cart/{cartItemId}/quantity")
    suspend fun changeQuantity(@Path("cartItemId") cartItemId: UUID, @Body cartItemQuantity: CartItemQuantity): Response<Map<String, String>>

    @DELETE("cart-item/{cartItemId}")
    suspend fun deleteCartItem(@Path("cartItemId") cartItemId: UUID): Response<Map<String, String>>

    @DELETE("cart-item/remove/{cartItemId}")
    suspend fun removeCartItem(@Path("cartItemId") cartItemId: UUID): Response<Map<String, String>>

    @POST("/order/create")
    suspend fun createOrder(@Body orderCreate: OrderCreate): Response<Map<String, String>>

    @GET("/order/user/{user_id}")
    suspend fun getOrderByUserId(@Path("user_id") user_id: UUID): List<OrderEntity>
}
