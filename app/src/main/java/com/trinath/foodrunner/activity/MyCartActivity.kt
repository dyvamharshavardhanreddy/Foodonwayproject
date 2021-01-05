package com.trinath.foodrunner.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.trinath.foodrunner.R
import com.trinath.foodrunner.adapter.CartRecyclerAdapter
import com.trinath.foodrunner.adapter.RestaurantDetailsRecyclerAdapter
import com.trinath.foodrunner.database.OrderEntity
import com.trinath.foodrunner.database.RestaurantDatabase
import com.trinath.foodrunner.model.RestaurantMenu
import com.trinath.foodrunner.utill.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList


class MyCartActivity : AppCompatActivity() {

    lateinit var recyclerOrderDetails: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var btnPlaceOrder: Button
    lateinit var toolbar: Toolbar
    var totalCost: Int = 0
    var resId:Int = 0
    lateinit var resName: String
    lateinit var txtCartRestaurantName:TextView

    var orderList = arrayListOf<RestaurantMenu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cart)
        init()
        setupToolbar()
        setUpCartList()
        placeOrder()

    }


    private fun init() {
        progressLayout = findViewById(R.id.progressLayout)
        val bundle = intent.getBundleExtra("data")
        resId = bundle?.getInt("resId", 0) as Int
        resName = bundle.getString("resName", "") as String
        txtCartRestaurantName = findViewById(R.id.txtCartRestaurantName)
        txtCartRestaurantName.text = resName
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpCartList() {
        recyclerOrderDetails = findViewById(R.id.recyclerOrderDetails)
        val dbList = GetItemsFromDBAsync(applicationContext).execute().get()

        for (element in dbList) {
            orderList.addAll(
                Gson().fromJson(element.foodItems, Array<RestaurantMenu>::class.java).asList()
            )
        }

        if (orderList.isEmpty()) {
            progressLayout.visibility = View.VISIBLE
        } else {
            progressLayout.visibility = View.GONE
        }

        recyclerAdapter = CartRecyclerAdapter( this,orderList)
        layoutManager = LinearLayoutManager(this)
        recyclerOrderDetails.layoutManager = layoutManager
        recyclerOrderDetails.itemAnimator = DefaultItemAnimator()
        recyclerOrderDetails.adapter = recyclerAdapter
    }


    private fun placeOrder() {
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)

        for (i in 0 until orderList.size) {
            totalCost += orderList[i].itemCost.toInt()
        }
        val total = "Place Order(Total: Rs. $totalCost)"
        btnPlaceOrder.text = total

        btnPlaceOrder.setOnClickListener {
            progressLayout.visibility = View.VISIBLE
            sendServerRequest()
        }
    }

    private fun sendServerRequest() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"

        val jsonParams = JSONObject()
        jsonParams.put(
            "user_id",
            this.getSharedPreferences(getString(R.string.register_preferences_file_name), Context.MODE_PRIVATE).getString(
                "register_id",
                null
            ) as String
        )
        jsonParams.put("restaurant_id", resId.toString())
        var sum:Int = 0
        for (i in 0 until orderList.size) {
            sum += orderList[i].itemCost.toInt()
        }
        jsonParams.put("total_cost", sum.toString())
        val foodArray = JSONArray()
        for (i in 0 until orderList.size) {
            val foodId = JSONObject()
            foodId.put("food_item_id", orderList[i].itemId)
            foodArray.put(i, foodId)
        }
        jsonParams.put("food", foodArray)

        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST,url, jsonParams, Response.Listener {

                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val clearCart =
                            ClearDBAsync(applicationContext, resId.toString()).execute().get()
                        RestaurantDetailsRecyclerAdapter.isCartEmpty = true

                        val intent = Intent(this,OrderConfirmedActivity::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(this@MyCartActivity, "Some Error occurred", Toast.LENGTH_SHORT)
                            .show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }, Response.ErrorListener {
                Toast.makeText(this@MyCartActivity, it.message, Toast.LENGTH_SHORT).show()
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "81e89712a2ba04"
                    return headers
                }
            }

        queue.add(jsonObjectRequest)

    }


    class GetItemsFromDBAsync(context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            return db.orderDao().getAllOrders()
        }

    }

    class ClearDBAsync(context: Context, val resId: String) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().deleteOrders(resId)
            db.close()
            return true
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val clearCart =
        ClearDBAsync(applicationContext, resId.toString()).execute().get()
        RestaurantDetailsRecyclerAdapter.isCartEmpty = true
        onBackPressed()
        return true
    }
}



