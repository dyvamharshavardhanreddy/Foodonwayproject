package com.trinath.foodrunner.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.provider.Telephony.Carriers.PASSWORD
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

class RegisterActivity : AppCompatActivity() {

    lateinit var etRegisterName:EditText
    lateinit var etRegisterEmail:EditText
    lateinit var etRegisterPhoneNumber:EditText
    lateinit var etRegisterAddress:EditText
    lateinit var etRegisterPassword:EditText
    lateinit var etRegisterConfirmPassword:EditText
    lateinit var btnRegister:Button
    lateinit var toolbar: Toolbar
    lateinit var sharedPreferences: SharedPreferences
    lateinit var responseId:String
    lateinit var responseName:String
    lateinit var responseEmail:String
    lateinit var responseMobileNumber:String
    lateinit var responseAddress:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(getString(R.string.register_preferences_file_name),Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false)
        setContentView(R.layout.activity_register)
        if(isLoggedIn){
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        etRegisterName = findViewById(R.id.etRegisterName)
        etRegisterEmail = findViewById(R.id.etRegisterEmail)
        etRegisterPhoneNumber = findViewById(R.id.etRegisterPhoneNumber)
        etRegisterAddress = findViewById(R.id.etRegisterAddress)
        etRegisterPassword = findViewById(R.id.etRegisterPassword)
        etRegisterConfirmPassword = findViewById(R.id.etRegisterConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        toolbar = findViewById(R.id.toolbar)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        btnRegister.setOnClickListener {
            if(TextUtils.isEmpty(etRegisterName.text.toString())){
                etRegisterName.error = "Required"
            }
            else if(TextUtils.isEmpty(etRegisterEmail.text.toString().trim()) && (!Patterns.EMAIL_ADDRESS.matcher(etRegisterEmail.text.toString().trim()).matches())){
                    etRegisterEmail.error = "Enter valid Email Id"
            }
            else if(TextUtils.isEmpty(etRegisterPhoneNumber.text.toString()) && (!Patterns.PHONE.matcher(etRegisterPhoneNumber.text.toString()).matches())){
                etRegisterPhoneNumber.error = "Enter valid Phone Number"
            }
            else if(TextUtils.isEmpty(etRegisterAddress.text.toString())){
                etRegisterAddress.error = "Required"
            }
            else if(TextUtils.isEmpty(etRegisterPassword.text.toString())){
                etRegisterPassword.error = "Required"
            }
            else if(TextUtils.isEmpty(etRegisterConfirmPassword.text.toString())){
                etRegisterConfirmPassword.error = "Required"
            }
            else if (!etRegisterPassword.text.toString().equals(etRegisterConfirmPassword.text.toString())){

                etRegisterConfirmPassword.error="doesn't Match with Password"
            }
            else{

                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/register/fetch_result"

                if(ConnectionManager().checkConnectivity(this)) {

                    val jsonParams = JSONObject()
                    jsonParams.put("name", etRegisterName.text.toString().trim())
                    jsonParams.put("mobile_number", etRegisterPhoneNumber.text.toString().trim())
                    jsonParams.put("password", etRegisterConfirmPassword.text.toString().trim())
                    jsonParams.put("address", etRegisterAddress.text.toString().trim())
                    jsonParams.put("email", etRegisterEmail.text.toString().trim())

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
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onPause() {
        super.onPause()
        finish()
    }


}
