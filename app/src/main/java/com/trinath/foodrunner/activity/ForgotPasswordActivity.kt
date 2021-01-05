package com.trinath.foodrunner.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.trinath.foodrunner.R
import com.trinath.foodrunner.utill.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var etForgotMobile:EditText
    lateinit var etForgotEmail:EditText
    lateinit var btnForgotNext:Button
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etForgotMobile = findViewById(R.id.etForgotMobile)
        etForgotEmail = findViewById(R.id.etForgotEmail)
        btnForgotNext = findViewById(R.id.btnForgotNext)
        toolbar =findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Forgot Password !!"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        btnForgotNext.setOnClickListener {
            if(TextUtils.isEmpty(etForgotMobile.text.toString())&&(!Patterns.PHONE.matcher(etForgotMobile.text.toString()).matches())){
                etForgotMobile.error = "Enter valid Mobile Number"
            }
            else if (TextUtils.isEmpty(etForgotEmail.text.toString()) && (!Patterns.EMAIL_ADDRESS.matcher(etForgotEmail.text.toString().trim()).matches())){
                etForgotEmail.error = "Enter valid Email Address"
            }
            else{
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                if(ConnectionManager().checkConnectivity(this)) {

                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", etForgotMobile.text.toString())
                    jsonParams.put("email", etForgotEmail.text.toString())

                    val jsonRegisterRequest =
                        object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                            Response.Listener {

                                try {
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")

                                    if (success) {
                                        val firstTry = data.getBoolean("first_try")
                                        if (!firstTry){
                                            Toast.makeText(this,"otp sent...",Toast.LENGTH_SHORT).show()
                                        }
                                            val intent = Intent(this,ResetPasswordActivity::class.java)
                                            intent.putExtra("phone",etForgotMobile.text.toString())
                                            startActivity(intent)

                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Enter valid credentials...",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                } catch (e: JSONException) {

                                    Toast.makeText(
                                        this,
                                        "some error occurred !!!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }


                            }, Response.ErrorListener {
                                Toast.makeText(
                                    this,
                                    "Volley error occurred !!!",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "81e89712a2ba04"
                                return headers
                            }

                        }
                    queue.add(jsonRegisterRequest)
                }else{
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection not Found")
                    dialog.setPositiveButton("Open Settings"){_,_ ->

                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()

                    }
                    dialog.setNegativeButton("Exit"){_,_ ->

                        ActivityCompat.finishAffinity(this)
                    }
                    dialog.create()
                    dialog.show()
                }


            }
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
