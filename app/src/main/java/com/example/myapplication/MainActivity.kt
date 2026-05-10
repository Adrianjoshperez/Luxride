package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        // 1. Find the button by its ID
        val loginBtn = findViewById<Button>(R.id.loginButton)

        // 2. Tell the button what to do when clicked
        loginBtn.setOnClickListener {

            // 3. Create an "Intent" to go from THIS screen to the ChooseLuxActivity screen
            val intent = Intent(this, ChooseLuxActivity::class.java)
            startActivity(intent)

        }
    }
}