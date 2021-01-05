package com.trinath.foodrunner.fragments


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.trinath.foodrunner.R
import com.trinath.foodrunner.adapter.OrderHistoryRecyclerAdapter
import com.trinath.foodrunner.model.OrderDetails
import com.trinath.foodrunner.utill.ConnectionManager
import org.json.JSONException

/**
 * A simple [Fragment] subclass.
 */
class OrderedHistoryFragment : Fragment() {

    lateinit var recyclerOrderHistoryDetails:RecyclerView
    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar:ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter
    val ordersInfoList = arrayListOf<OrderDetails>()
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        sharedPreferences = context!!.getSharedPreferences(getString(R.string.register_preferences_file_name),
            Context.MODE_PRIVATE)
        val view = inflater.inflate(R.layout.fragment_ordered_history, container, false)

        val userId = sharedPreferences.getString("register_id","Id")

        recyclerOrderHistoryDetails = view.findViewById(R.id.recyclerOrderHistoryDetails)
        layoutManager = LinearLayoutManager(activity)

        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)

        progressLayout.visibility = View.VISIBLE
        (context as AppCompatActivity).supportActionBar?.title = "Order History"


        if (userId!="Id") {
            val queue = Volley.newRequestQueue(activity as Context)
            val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"


            if (ConnectionManager().checkConnectivity(activity as Context)) {

                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url,
                    null, Response.Listener {

                        try {
                            progressLayout.visibility = View.GONE

                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")

                            if (success) {

                                val dataArr = data.getJSONArray("data")
                                for (i in 0 until dataArr.length()) {
                                    val orderJsonObject = dataArr.getJSONObject(i)
                                    val orderObject = OrderDetails(
                                        orderJsonObject.getString("order_id"),
                                        orderJsonObject.getString("restaurant_name"),
                                        orderJsonObject.getString("total_cost"),
                                        orderJsonObject.getString("order_placed_at"),
                                        orderJsonObject.getJSONArray("food_items")
                                    )

                                    ordersInfoList.add(orderObject)

                                    recyclerAdapter = OrderHistoryRecyclerAdapter(activity as Context, ordersInfoList)

                                    recyclerOrderHistoryDetails.adapter = recyclerAdapter

                                    recyclerOrderHistoryDetails.layoutManager = layoutManager

                                }
                            } else {
                                Toast.makeText(
                                    activity as Context,
                                    "Some error has occurred!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                activity as Context,
                                "Some Unexpected error Occurred !!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }, Response.ErrorListener {

                        if (activity != null) {

                            Toast.makeText(
                                activity as Context,
                                "Volley error occurred !!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "81e89712a2ba04"
                        return headers
                    }
                }

                queue.add(jsonObjectRequest)
            } else {
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection not Found")
                dialog.setPositiveButton("Open Settings") { _, _ ->

                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    activity?.finish()

                }
                dialog.setNegativeButton("Exit") { _, _ ->

                    ActivityCompat.finishAffinity(activity as Activity)
                }
                dialog.create()
                dialog.show()
            }
        }else{
                Toast.makeText(activity as Context,
                    "User Id not received !!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        return view
    }

}



