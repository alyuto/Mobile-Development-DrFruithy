package com.dicoding.drfruithy

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.drfruithy.data.Repository
import com.dicoding.drfruithy.data.pref.UserModel

class MainViewModel(private val repository: Repository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}