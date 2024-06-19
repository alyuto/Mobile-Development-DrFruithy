package com.dicoding.drfruithy.data.pref

data class UserModel (
    val username: String,
    val token: String,
    val isLogin: Boolean = false
    )