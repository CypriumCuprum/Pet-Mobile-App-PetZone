package com.example.petapp.data.api

import com.example.petapp.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = BuildConfig.SERVER_URL
    val apiService: ApiService by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)  // Tăng thời gian timeout kết nối
            .readTimeout(30, TimeUnit.SECONDS)     // Tăng thời gian timeout đọc dữ liệu
            .writeTimeout(30, TimeUnit.SECONDS)    // Tăng thời gian timeout ghi dữ liệu
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}