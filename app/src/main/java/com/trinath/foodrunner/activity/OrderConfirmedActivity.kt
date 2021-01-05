package com.trinath.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.trinath.foodrunner.R

class OrderConfirmedActivity : AppCompatActivity() {

    lateinit var btnOk:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmed)
        btnOk = findViewById(R.id.btnOk)
        btnOk.setOnClickListener {
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)

        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
