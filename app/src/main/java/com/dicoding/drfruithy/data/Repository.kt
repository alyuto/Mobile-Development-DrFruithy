package com.dicoding.drfruithy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.drfruithy.data.api.ApiService
import com.dicoding.drfruithy.data.pref.ResultData
import com.dicoding.drfruithy.data.pref.UserModel
import com.dicoding.drfruithy.data.pref.UserPreference
import com.dicoding.drfruithy.data.response.LoginResponse
import com.dicoding.drfruithy.data.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class Repository private constructor(
    private var apiService: ApiService,
    private val userPreference: UserPreference
){

    fun updateApiService(apiService: ApiService) {
        this.apiService = apiService
    }
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    // New methods for theme settings
    fun getThemeSetting(): Flow<Boolean> {
        return userPreference.getThemeSetting()
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        userPreference.saveThemeSetting(isDarkModeActive)
    }

    fun signUpUser(name: String, password: String) = liveData {
        emit(ResultData.Loading)
        try {
            val successResponse = apiService.register(name, password)
            emit(ResultData.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
            val errorMessage = errorResponse?.password ?: "Unknown error"
            emit(ResultData.Error(errorMessage))
        }
    }

    fun loginUser(username: String, password: String): LiveData<ResultData<LoginResponse>> {
        return liveData {
            emit(ResultData.Loading)
            try {
                val response = apiService.login(username, password)
                emit(ResultData.Success(response))
            } catch (e: Exception) {
                emit(ResultData.Error(e.message ?: "Unknown error"))
            }
        }
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(apiService: ApiService,userPreference: UserPreference): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, userPreference)
            }.also { instance = it }
    }
}
