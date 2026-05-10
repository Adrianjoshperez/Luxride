package com.example.luxride

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class Profile : AppCompatActivity() {

    // ── View references ──────────────────────────────────────────────────────
    private lateinit var cvCameraBadge:   CardView
    private lateinit var ivAvatar:        ImageView
    private lateinit var tvAvatarInitial: TextView

    private lateinit var tilDisplayName:  TextInputLayout
    private lateinit var tilDateOfBirth:  TextInputLayout
    private lateinit var tilGender:       TextInputLayout
    private lateinit var tilAddress:      TextInputLayout
    private lateinit var tilCity:         TextInputLayout

    private lateinit var etDisplayName:   TextInputEditText
    private lateinit var etDateOfBirth:   TextInputEditText
    private lateinit var etAddress:       TextInputEditText
    private lateinit var etCity:          TextInputEditText
    private lateinit var actvGender:      AutoCompleteTextView

    private lateinit var switchQuietRide: SwitchMaterial
    private lateinit var switchClimate:   SwitchMaterial

    private lateinit var btnContinue:     Button
    private lateinit var tvSkip:          TextView

    // ── State ────────────────────────────────────────────────────────────────
    private var selectedAvatarUri: Uri? = null

    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { onPhotoSelected(it) }
        }

    // ── Lifecycle ────────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViews()
        prefillFromRegistration()
        setupGenderDropdown()
        attachWatchers()
        setupClickListeners()
    }

    // ── Bind ─────────────────────────────────────────────────────────────────
    private fun bindViews() {
        cvCameraBadge   = findViewById(R.id.cvCameraBadge)
        ivAvatar        = findViewById(R.id.ivAvatar)
        tvAvatarInitial = findViewById(R.id.tvAvatarInitial)

        tilDisplayName  = findViewById(R.id.tilDisplayName)
        tilDateOfBirth  = findViewById(R.id.tilDateOfBirth)
        tilGender       = findViewById(R.id.tilGender)
        tilAddress      = findViewById(R.id.tilAddress)
        tilCity         = findViewById(R.id.tilCity)

        etDisplayName   = findViewById(R.id.etDisplayName)
        etDateOfBirth   = findViewById(R.id.etDateOfBirth)
        etAddress       = findViewById(R.id.etAddress)
        etCity          = findViewById(R.id.etCity)
        actvGender      = findViewById(R.id.actvGender)

        switchQuietRide = findViewById(R.id.switchQuietRide)
        switchClimate   = findViewById(R.id.switchClimate)

        btnContinue     = findViewById(R.id.btnContinue)
        tvSkip          = findViewById(R.id.tvSkip)
    }

    // ── Pre-fill name passed from Register ───────────────────────────────────
    private fun prefillFromRegistration() {
        val fullName = intent.getStringExtra(EXTRA_FULL_NAME) ?: return
        etDisplayName.setText(fullName)
        updateAvatarInitial(fullName)
    }

    // ── Gender dropdown ──────────────────────────────────────────────────────
    private fun setupGenderDropdown() {
        val options = listOf("Prefer not to say", "Male", "Female", "Non-binary", "Other")
        actvGender.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, options)
        )
    }

    // ── Clear errors on typing ───────────────────────────────────────────────
    private fun attachWatchers() {
        listOf(etDisplayName to tilDisplayName,
            etAddress     to tilAddress,
            etCity        to tilCity
        ).forEach { (et, til) ->
            et.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    til.error = null
                    if (et == etDisplayName) updateAvatarInitial(s.toString())
                }
                override fun afterTextChanged(s: Editable?) = Unit
            })
        }
    }

    // ── Click listeners ──────────────────────────────────────────────────────
    private fun setupClickListeners() {
        cvCameraBadge.setOnClickListener {
            photoPickerLauncher.launch("image/*")
        }

        etDateOfBirth.setOnClickListener { showDatePicker() }
        tilDateOfBirth.setEndIconOnClickListener { showDatePicker() }

        btnContinue.setOnClickListener {
            if (validateForm()) saveAndContinue()
        }

        tvSkip.setOnClickListener {
            navigateToHome()
        }
    }

    // ── Avatar helpers ───────────────────────────────────────────────────────
    private fun updateAvatarInitial(name: String) {
        if (selectedAvatarUri != null) return
        tvAvatarInitial.text = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    }

    private fun onPhotoSelected(uri: Uri) {
        selectedAvatarUri = uri
        ivAvatar.setImageURI(uri)
        tvAvatarInitial.text = ""
    }

    // ── Date picker ──────────────────────────────────────────────────────────
    private fun showDatePicker() {
        val cal = Calendar.getInstance().apply { add(Calendar.YEAR, -25) }
        DatePickerDialog(
            this,
            { _, year, month, day ->
                etDateOfBirth.setText(
                    String.format("%02d / %02d / %04d", day, month + 1, year)
                )
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }.show()
    }

    // ── Validation ───────────────────────────────────────────────────────────
    private fun validateForm(): Boolean {
        var isValid = true

        val displayName = etDisplayName.text.toString().trim()
        if (displayName.isEmpty()) {
            tilDisplayName.error = "Display name is required"
            isValid = false
        } else if (displayName.length < 2) {
            tilDisplayName.error = "Enter a valid name"
            isValid = false
        }

        val address = etAddress.text.toString().trim()
        val city    = etCity.text.toString().trim()
        if (address.isNotEmpty() && city.isEmpty()) {
            tilCity.error = "Please enter a city"
            isValid = false
        }
        if (city.isNotEmpty() && address.isEmpty()) {
            tilAddress.error = "Please enter a street address"
            isValid = false
        }

        return isValid
    }

    // ── Save & navigate ──────────────────────────────────────────────────────
    private fun saveAndContinue() {
        btnContinue.isEnabled = false
        btnContinue.text = "Saving…"

        // Placeholder — replace with real backend / SharedPreferences / Room save
        btnContinue.postDelayed({
            onSaveSuccess()
        }, 1000)
    }

    private fun onSaveSuccess() {
        Toast.makeText(this, "Profile saved — welcome to LuxRide!", Toast.LENGTH_SHORT).show()
        btnContinue.isEnabled = true
        btnContinue.text      = "SAVE & CONTINUE"
        navigateToHome()
    }

    private fun onSaveFailure(message: String?) {
        btnContinue.isEnabled = true
        btnContinue.text      = "SAVE & CONTINUE"
        Toast.makeText(this, message ?: "Save failed. Try again.", Toast.LENGTH_LONG).show()
    }

    private fun navigateToHome() {
        // Uncomment when HomeActivity exists:
        // startActivity(Intent(this, HomeActivity::class.java))
        // finishAffinity()
        Toast.makeText(this, "Navigating to Home…", Toast.LENGTH_SHORT).show()
    }

    // ── Companion — launch helper from Register ──────────────────────────────
    companion object {
        const val EXTRA_FULL_NAME = "extra_full_name"

        fun startFrom(source: AppCompatActivity, fullName: String) {
            source.startActivity(
                Intent(source, Profile::class.java).apply {
                    putExtra(EXTRA_FULL_NAME, fullName)
                }
            )
        }
    }
}