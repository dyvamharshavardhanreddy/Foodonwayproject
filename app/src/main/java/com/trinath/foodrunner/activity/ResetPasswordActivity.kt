package com.trinath.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
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

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var etResetOtp:EditText
    lateinit var etResetNewPassword:EditText
    lateinit var etResetConfirmPassword:EditText
    lateinit var btnResetSubmit:Button
    lateinit var toolbar:Toolbar
    var phone:String?=null
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(getString(R.string.register_preferences_file_name),
            Context.MODE_PRIVATE)
        setContentView(R.layout.activity_reset_password)

        if(intent!=null){
            phone = intent.getStringExtra("phone")
        }

        toolbar = findViewById(R.id.toolbar)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        etResetOtp = findViewById(R.id.etResetOtp)
        etResetNewPassword = findViewById(R.id.etResetNewPassword)
        etResetConfirmPassword = findViewById(R.id.etResetConfirmPassword)
        btnResetSubmit = findViewById(R.id.btnResetSubmit)

        Toast.makeText(this,"$phone",Toast.LENGTH_SHORT).show()
        btnResetSubmit.setOnClickListener {
            if (TextUtils.isEmpty(etResetOtp.text.toString())&&(etResetOtp.text.toString().length!=4)){
                etResetOtp.error = "Enter valid Otp"
            }
            else if(TextUtils.isEmpty(etResetNewPassword.text.toString())){
                etResetNewPassword.error = "Required"
            }
            else if(TextUtils.isEmpty(etResetConfirmPassword.text.toString())){
                etResetConfirmPassword.error = "Required"
            }
            else if (!etResetNewPassword.text.toString().equals(etResetConfirmPassword.text.toString())){
                etResetConfirmPassword.error="doesn't Match with Password"
            }
            else{
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                if(ConnectionManager().checkConnectivity(this)) {

                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", "$phone")
                    jsonParams.put("password", etResetConfirmPassword.text.toString())
                    jsonParams.put("otp",etResetOtp.text.toString())

                    val jsonRegisterRequest =
                        object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                            Response.Listener {

                                try {
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")

                                    if (success) {
                                        val message = data.getString("successMessage")
                                        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this,LoginActivity::class.java)
                                        sharedPreferences.edit().clear().apply()
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
