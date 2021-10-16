package com.example.comana

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BikeRideActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bikeride)

        val actionBar = supportActionBar
        actionBar!!.title = "Second Activity"
        actionBar.setDisplayHomeAsUpEnabled(true)
    }
}