package com.trinath.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.trinath.foodrunner.R

class SplashscreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        Handler().postDelayed(Runnable {
            val intent = Intent(this,
                LoginActivity::class.java)
            startActivity(intent)
            finish()
        },2000)
    }
}
