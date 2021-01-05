package com.trinath.foodrunner.fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import com.trinath.foodrunner.R

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    lateinit var txtProfileName:TextView
    lateinit var txtProfilephone:TextView
    lateinit var txtProfileEmail:TextView
    lateinit var txtProfileAddress:TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

         val view =  inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPreferences = context!!.getSharedPreferences(getString(R.string.register_preferences_file_name),Context.MODE_PRIVATE)

        (context as AppCompatActivity).supportActionBar?.title = "Profile"

        txtProfileName = view.findViewById(R.id.txtProfileName)
        txtProfilephone = view.findViewById(R.id.txtProfilePhone)
        txtProfileEmail = view.findViewById(R.id.txtProfileEmail)
        txtProfileAddress = view.findViewById(R.id.txtProfileAddress)

        val name = sharedPreferences.getString("register_name", "Name")
        txtProfileName.text = name

        val phone = sharedPreferences.getString("register_phone", "Phone")
        txtProfilephone.text = phone

        val email = sharedPreferences.getString("register_email", "Email")
        txtProfileEmail.text = email

        val address = sharedPreferences.getString("register_address", "Address")
        txtProfileAddress.text = address

        return view
    }


}
