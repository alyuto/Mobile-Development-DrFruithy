package com.dicoding.drfruithy.ui.register

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {

    enum class InputType {
        NAME, EMAIL, PASSWORD
    }

    var inputType: InputType = InputType.NAME
        set(value) {
            field = value
            updateHint()
        }

    private var textInputLayout: TextInputLayout? = null

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInputLayout?.error = when (inputType) {
                    InputType.NAME -> if (s.isNullOrEmpty()) "Nama harus diisi" else null
                    InputType.EMAIL -> if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) "Masukkan Format Email Dengan Benar" else null
                    InputType.PASSWORD -> if ((s?.length ?: 0) < 8) "Password harus minimal 8 karakter" else null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateHint() {
        hint = when (inputType) {
            InputType.NAME -> "Masukkan Nama"
            InputType.EMAIL -> "Masukkan Email"
            InputType.PASSWORD -> "Masukkan Password"
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    fun setupValidation(textInputLayout: TextInputLayout) {
        this.textInputLayout = textInputLayout
    }
}