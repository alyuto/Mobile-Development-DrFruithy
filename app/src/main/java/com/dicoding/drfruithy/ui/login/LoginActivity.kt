package com.dicoding.drfruithy.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.drfruithy.MainActivity
import com.dicoding.drfruithy.ViewModelFactory
import com.dicoding.drfruithy.data.api.ApiConfig
import com.dicoding.drfruithy.data.pref.ResultData
import com.dicoding.drfruithy.data.pref.UserModel
import com.dicoding.drfruithy.databinding.ActivityLoginBinding
import com.dicoding.drfruithy.di.Injection

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        setupTextWatchers()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val username = binding.emailEditText.text.toString() // Menggunakan username sebagai pengganti email
            val password = binding.passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                showToast("Username dan password harus diisi") // Mengganti pesan error
                return@setOnClickListener
            }
            viewModel.loginUser(username, password).observe(this) { result ->
                when (result) {
                    is ResultData.Loading -> {
                        showLoading(true)
                    }
                    is ResultData.Success -> {
                        val loginResponse = result.data
                        val user = UserModel(loginResponse.username ?: "", loginResponse.password ?: "")
                        viewModel.saveSession(user)
                        showSuccessDialog()
                        showLoading(false)
                    }
                    is ResultData.Error -> {
                        showToast(result.error)
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val username = binding.emailEditText.text.toString().trim() // Menggunakan username
                val password = binding.passwordEditText.text.toString().trim()
                binding.loginButton.isEnabled = username.isNotEmpty() && password.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.emailEditText.addTextChangedListener(textWatcher)
        binding.passwordEditText.addTextChangedListener(textWatcher)

        binding.loginButton.isEnabled = false
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Success")
            setMessage("Login berhasil. Selamat datang!")
            setPositiveButton("OK") { _, _ ->
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
            create()
            show()
        }
    }
}
