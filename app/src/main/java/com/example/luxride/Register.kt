package com.example.luxride

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class Register : AppCompatActivity() {

    // ── View references ──────────────────────────────────────────────────────
    private lateinit var tilFullName:        TextInputLayout
    private lateinit var tilEmail:           TextInputLayout
    private lateinit var tilPhone:           TextInputLayout
    private lateinit var tilPassword:        TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout

    private lateinit var etFullName:         TextInputEditText
    private lateinit var etEmail:            TextInputEditText
    private lateinit var etPhone:            TextInputEditText
    private lateinit var etPassword:         TextInputEditText
    private lateinit var etConfirmPassword:  TextInputEditText
    private lateinit var cbTerms:            CheckBox
    private lateinit var btnRegister:        Button
    private lateinit var tvSignIn:           TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViews()
        attachWatchers()
        setupClickListeners()
    }

    private fun bindViews() {
        tilFullName        = findViewById(R.id.tilFullName)
        tilEmail           = findViewById(R.id.tilEmail)
        tilPhone           = findViewById(R.id.tilPhone)
        tilPassword        = findViewById(R.id.tilPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)

        etFullName         = findViewById(R.id.etFullName)
        etEmail            = findViewById(R.id.etEmail)
        etPhone            = findViewById(R.id.etPhone)
        etPassword         = findViewById(R.id.etPassword)
        etConfirmPassword  = findViewById(R.id.etConfirmPassword)

        cbTerms            = findViewById(R.id.cbTerms)
        btnRegister        = findViewById(R.id.btnRegister)
        tvSignIn           = findViewById(R.id.tvSignIn)
    }

    private fun attachWatchers() {
        val pairs = listOf(
            etFullName        to tilFullName,
            etEmail           to tilEmail,
            etPhone           to tilPhone,
            etPassword        to tilPassword,
            etConfirmPassword to tilConfirmPassword
        )
        pairs.forEach { (et, til) ->
            et.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    til.error = null
                }
                override fun afterTextChanged(s: Editable?) = Unit
            })
        }
    }

    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            if (validateForm()) {
                performRegistration()
            }
        }

        tvSignIn.setOnClickListener {
            finish()
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        val fullName        = etFullName.text.toString().trim()
        val email           = etEmail.text.toString().trim()
        val phone           = etPhone.text.toString().trim()
        val password        = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        // Full name
        if (fullName.isEmpty()) {
            tilFullName.error = "Full name is required"
            isValid = false
        } else if (fullName.length < 2) {
            tilFullName.error = "Enter a valid name"
            isValid = false
        }

        // Email
        if (email.isEmpty()) {
            tilEmail.error = "Email address is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Enter a valid email address"
            isValid = false
        }

        // Phone
        if (phone.isEmpty()) {
            tilPhone.error = "Phone number is required"
            isValid = false
        } else if (phone.length < 7) {
            tilPhone.error = "Enter a valid phone number"
            isValid = false
        }

        if (password.isEmpty()) {
            tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 8) {
            tilPassword.error = "Password must be at least 8 characters"
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.error = "Please confirm your password"
            isValid = false
        } else if (confirmPassword != password) {
            tilConfirmPassword.error = "Passwords do not match"
            isValid = false
        }
        if (!cbTerms.isChecked) {
            Toast.makeText(this, "Please agree to the Terms & Privacy Policy", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun performRegistration() {
        val fullName = etFullName.text.toString().trim()
        val email    = etEmail.text.toString().trim()
        val phone    = etPhone.text.toString().trim()
        val password = etPassword.text.toString()

        btnRegister.isEnabled = false
        btnRegister.text = "Creating Account..."


        // Placeholder — remove when connecting real backend
        btnRegister.postDelayed({
            onRegistrationSuccess()
        }, 1500)
    }

    private fun onRegistrationSuccess() {
        Toast.makeText(this, "Welcome to LuxRide!", Toast.LENGTH_SHORT).show()
        btnRegister.isEnabled = true
        btnRegister.text = "CREATE ACCOUNT"
        // Navigate to Home / Dashboard
        // startActivity(Intent(this, HomeActivity::class.java))
        // finish()
    }

    private fun onRegistrationFailure(message: String?) {
        btnRegister.isEnabled = true
        btnRegister.text = "CREATE ACCOUNT"
        Toast.makeText(this, message ?: "Registration failed. Try again.", Toast.LENGTH_LONG).show()
    }
}