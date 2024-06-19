package com.dicoding.drfruithy.ui.register

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.drfruithy.ViewModelFactory
import com.dicoding.drfruithy.data.pref.ResultData
import com.dicoding.drfruithy.databinding.ActivityRegisterBinding
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupValidation()
        setupAction()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (isFormValid()) {
                viewModel.signupUser(name, password).observe(this) { result ->
                    when (result) {
                        is ResultData.Loading -> {
                            showLoading(true)
                        }

                        is ResultData.Success -> {
                            showSuccessDialog(name)
                            showLoading(false)
                        }

                        is ResultData.Error -> {
                            showToast(result.error)
                            showLoading(false)
                        }
                    }
                }
            } else {
                showErrorDialog()
            }
        }
    }

    private fun isFormValid(): Boolean {
        var isValid = true

        if (binding.nameEditText.text.isNullOrEmpty()) {
            binding.nameEditTextLayout.error = "Nama harus diisi"
            isValid = false
        } else {
            binding.nameEditTextLayout.error = null
        }

        val password = binding.passwordEditText.text.toString()
        if (password.length < 8) {
            binding.passwordEditTextLayout.error = "Password harus minimal 8 karakter"
            isValid = false
        } else {
            binding.passwordEditTextLayout.error = null
        }

        return isValid
    }

    private fun showSuccessDialog(email: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Akun dengan Username $email sudah jadi nih. Yuk, login.")
            setPositiveButton("Lanjut") { _, _ ->
                finish()
            }
            create()
            show()
        }
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage("Please fill in all fields correctly.")
            setPositiveButton("OK", null)
            create()
            show()
        }
    }

    private fun setupValidation() {
        setupCustomEditText(binding.nameEditText, binding.nameEditTextLayout, CustomEditText.InputType.NAME)
        setupCustomEditText(binding.passwordEditText, binding.passwordEditTextLayout, CustomEditText.InputType.PASSWORD)
    }

    private fun setupCustomEditText(editText: CustomEditText, textInputLayout: TextInputLayout, inputType: CustomEditText.InputType) {
        editText.inputType = inputType
        editText.setupValidation(textInputLayout)
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
