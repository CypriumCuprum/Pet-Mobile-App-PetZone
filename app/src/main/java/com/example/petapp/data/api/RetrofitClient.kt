package com.example.petapp.data.api

import com.example.petapp.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = BuildConfig.SERVER_URL

    private var retrofitInstance: Retrofit? = null
    private var apiServiceInstance: ApiService? = null
    val apiService: ApiService by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)  // Tăng thời gian timeout kết nối
            .readTimeout(30, TimeUnit.SECONDS)     // Tăng thời gian timeout đọc dữ liệu
            .writeTimeout(30, TimeUnit.SECONDS)    // Tăng thời gian timeout ghi dữ liệu
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    // Truy cập lazy cho ApiService
    val instance: ApiService
        get() {
            if (apiServiceInstance == null) {
                apiServiceInstance = createRetrofit().create(ApiService::class.java)
            }
            return apiServiceInstance!!
        }

    // Tạo instance Retrofit mới với URL hiện tại
    private fun createRetrofit(): Retrofit {
        if (retrofitInstance == null) {
            retrofitInstance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofitInstance!!
    }
}