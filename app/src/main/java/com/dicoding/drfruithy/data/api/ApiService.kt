package com.dicoding.drfruithy.data.api

import com.dicoding.drfruithy.data.response.DetectResponse
import com.dicoding.drfruithy.data.response.LoginResponse
import com.dicoding.drfruithy.data.response.RegisterResponse
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse

    @Multipart
    @POST("predict/apple")
    suspend fun detectApple(
        @Part image: MultipartBody.Part
    ): DetectResponse

    @Multipart
    @POST("predict/banana")
    suspend fun detectBanana(
        @Part image: MultipartBody.Part
    ): DetectResponse
}