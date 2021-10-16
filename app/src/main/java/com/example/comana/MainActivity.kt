package com.example.comana

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newGroupButton = findViewById<Button>(R.id.new_group_button)
        newGroupButton.setOnClickListener {
            val intent = Intent(this@MainActivity, BikeRideActivity::class.java)
            startActivity(intent)
        }
    }
}