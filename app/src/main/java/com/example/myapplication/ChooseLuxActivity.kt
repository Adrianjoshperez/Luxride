package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity

class ChooseLuxActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // This tells Android to show your Screen 4 layout!
        // Make sure your XML file is named exactly like this:
        setContentView(R.layout.activity_choose_lux)

    }
}