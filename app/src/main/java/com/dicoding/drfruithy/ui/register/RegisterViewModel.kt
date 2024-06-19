package com.dicoding.drfruithy.ui.register

import androidx.lifecycle.ViewModel
import com.dicoding.drfruithy.data.Repository

class RegisterViewModel(private val repository: Repository) : ViewModel(){
    fun signupUser(name: String, password: String) =
        repository.signUpUser(name, password)
}