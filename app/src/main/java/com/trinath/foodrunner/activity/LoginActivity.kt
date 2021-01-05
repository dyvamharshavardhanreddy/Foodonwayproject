package com.trinath.foodrunner.activity

import android.Manifest.permission_group.PHONE
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PatternMatcher
import android.provider.Settings
import android.text.TextUtils
import android.util.Patterns
import android.util.Patterns.PHONE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.trinath.foodrunner.R
import com.trinath.foodrunner.utill.ConnectionManager
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    lateinit var etLoginMobileNumber:EditText
    lateinit var etLoginPassword:EditText
    lateinit var txtLoginForgotPassword:TextView
    lateinit var txtLoginSignUp:TextView
    lateinit var btnLogin:Button
    lateinit var responseId:String
    lateinit var responseName:String
    lateinit var responseEmail:String
    lateinit var responseMobileNumber:String
    lateinit var responseAddress:String
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(getString(R.string.register_preferences_file_name),
            Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false)
        setContentView(R.layout.activity_login)
        if(isLoggedIn){
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        etLoginMobileNumber = findViewById(R.id.etLoginMobileNumber)
        etLoginPassword = findViewById(R.id.etLoginPassword)
        txtLoginForgotPassword = findViewById(R.id.txtLoginForgotPassword)
        txtLoginSignUp = findViewById(R.id.txtLoginSignUp)
        btnLogin = findViewById(R.id.btnLogin)
        txtLoginForgotPassword.setOnClickListener {
            val intent = Intent(this,ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        txtLoginSignUp.setOnClickListener {

            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            if(TextUtils.isEmpty(etLoginMobileNumber.text.toString())&&(!Patterns.PHONE.matcher(etLoginMobileNumber.text.toString()).matches())){
                etLoginMobileNumber.error = "Enter valid Mobile Number"
            }
            else if (TextUtils.isEmpty(etLoginPassword.text.toString())){
                etLoginPassword.error = "Required"
            }
            else{

                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/login/fetch_result"

                if(ConnectionManager().checkConnectivity(this)) {

                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", etLoginMobileNumber.text.toString())
                    jsonParams.put("password", etLoginPassword.text.toString())

                    val jsonRegisterRequest =
                        object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                            Response.Listener {

                                try {
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")

                                    if (success) {
                                        val personJsonObject = data.getJSONObject("data")

                                        responseId = personJsonObject.getString("user_id")
                                        responseName = personJsonObject.getString("name")
                                        responseEmail = personJsonObject.getString("email")
                                        responseMobileNumber = personJsonObject.getString("mobile_number")
                                        responseAddress = personJsonObject.getString("address")

                                        sharedPreferences.edit().putBoolean("isLoggedIn",success).apply()
                                        sharedPreferences.edit().putString("register_id",responseId).apply()
                                        sharedPreferences.edit().putString("register_name",responseName).apply()
                                        sharedPreferences.edit().putString("register_phone",responseMobileNumber).apply()
                                        sharedPreferences.edit().putString("register_email",responseEmail).apply()
                                        sharedPreferences.edit().putString("register_address",responseAddress).apply()

                                        val intent = Intent(this,HomeActivity::class.java)
                                        startActivity(intent)
                                        finish()

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
                    dialog.setPositiveButton("Open Settings"){_, _ ->

                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()

                    }
                    dialog.setNegativeButton("Exit"){_, _ ->

                        ActivityCompat.finishAffinity(this)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }
    }

}
