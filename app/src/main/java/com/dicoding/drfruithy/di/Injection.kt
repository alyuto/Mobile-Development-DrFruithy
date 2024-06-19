package com.dicoding.drfruithy.di

import android.content.Context
import com.dicoding.drfruithy.data.Repository
import com.dicoding.drfruithy.data.api.ApiConfig
import com.dicoding.drfruithy.data.pref.UserPreference
import com.dicoding.drfruithy.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return Repository.getInstance(apiService,pref)
    }
}