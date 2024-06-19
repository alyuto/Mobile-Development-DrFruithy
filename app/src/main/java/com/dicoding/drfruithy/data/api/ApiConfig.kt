package com.dicoding.drfruithy.data.api

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit


object ApiConfig {
    fun getApiService(token: String): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val requestHeaders = req.newBuilder().addHeader("Authorization", "Bearer $token").build()
            chain.proceed(requestHeaders)
        }
        val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor).addInterceptor(authInterceptor).build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://34.101.59.153:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }

    fun getDetectService(): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://drfruithy-pa5lvlwi4a-et.a.run.app/")
            .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper().registerModule(KotlinModule())))
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }
}