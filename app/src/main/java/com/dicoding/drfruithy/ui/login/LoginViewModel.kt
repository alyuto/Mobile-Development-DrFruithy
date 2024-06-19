package com.dicoding.drfruithy.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.drfruithy.data.Repository
import com.dicoding.drfruithy.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository): ViewModel() {
    fun saveSession(user: UserModel){
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun loginUser(username: String, password: String) = repository.loginUser(username, password)
}