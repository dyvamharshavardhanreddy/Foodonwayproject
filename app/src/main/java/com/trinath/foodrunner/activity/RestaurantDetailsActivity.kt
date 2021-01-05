package com.trinath.foodrunner.activity



import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.AsyncTask.execute
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.trinath.foodrunner.R
import com.trinath.foodrunner.adapter.RestaurantDetailsRecyclerAdapter
import com.trinath.foodrunner.database.OrderEntity
import com.trinath.foodrunner.database.RestaurantDatabase
import com.trinath.foodrunner.model.RestaurantMenu
import com.trinath.foodrunner.utill.ConnectionManager
import org.json.JSONException


class RestaurantDetailsActivity : AppCompatActivity() {

    lateinit var recyclerRestaurentDetails: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantDetailsRecyclerAdapter
    lateinit var progresslayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var toolbar: Toolbar
    var restaurantId: String? = "1000"
    var restaurantName: String? = "Restaurant"
    lateinit var btnProceedToCart: Button
    lateinit var sharedPreferences: SharedPreferences

    var restaurantMenuList = arrayListOf<RestaurantMenu>()
    var orderList = arrayListOf<RestaurantMenu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.id_preferences), Context.MODE_PRIVATE)

        setContentView(R.layout.activity_restaurants_details)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        recyclerRestaurentDetails = findViewById(R.id.recyclerRestaurantDetails)
        layoutManager = LinearLayoutManager(this)
        progressBar = findViewById(R.id.progressbar)
        progresslayout = findViewById(R.id.progresslayout)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        btnProceedToCart.visibility = View.GONE
        progresslayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE


        if (intent != null) {
            restaurantId = intent.getStringExtra("id")
            restaurantName = intent.getStringExtra("name")
        } else {
            finish()
            Toast.makeText(this, "Data not received in Intent !", Toast.LENGTH_SHORT).show()
        }

        supportActionBar?.title = "$restaurantName"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnProceedToCart.setOnClickListener {
            proceedToCart()
        }

        if (restaurantId == "1000") {
            finish()
            Toast.makeText(this, "Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
        }

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId/"

        if (ConnectionManager().checkConnectivity(this)) {

            val jsonRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {


                    try {
                        progresslayout.visibility = View.GONE
                        progressBar.visibility = View.GONE

                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {
                            val dataarr = data.getJSONArray("data")
                            for (i in 0 until dataarr.length()) {
                                val restaurantMenuJsonObject = dataarr.getJSONObject(i)
                                val foodItem = RestaurantMenu(
                                    restaurantMenuJsonObject.getString("id"),
                                    restaurantMenuJsonObject.getString("name"),
                                    restaurantMenuJsonObject.getString("cost_for_one")
                                )
                                sharedPreferences.edit().putString("restaurant_id", restaurantId)
                                    .apply()
                                restaurantMenuList.add(foodItem)

                                recyclerAdapter = RestaurantDetailsRecyclerAdapter(
                                    this,
                                    restaurantMenuList,
                                    object : RestaurantDetailsRecyclerAdapter.OnItemClickListener {
                                        override fun onAddItemClick(foodItem: RestaurantMenu) {
                                            orderList.add(foodItem)
                                            if (orderList.size > 0) {
                                                btnProceedToCart.visibility = View.VISIBLE
                                                RestaurantDetailsRecyclerAdapter.isCartEmpty = false
                                            }
                                        }

                                        override fun onRemoveItemClick(foodItem: RestaurantMenu) {
                                            orderList.remove(foodItem)
                                            if (orderList.isEmpty()) {
                                                btnProceedToCart.visibility = View.GONE
                                                RestaurantDetailsRecyclerAdapter.isCartEmpty = true
                                            }
                                        }
                                    })

                                recyclerRestaurentDetails.adapter = recyclerAdapter
                                recyclerRestaurentDetails.layoutManager = layoutManager

                            }
                        } else {
                            Toast.makeText(this, "Some error has occurred!!!", Toast.LENGTH_SHORT)
                                .show()
                        }


                    } catch (e: JSONException) {
                        Toast.makeText(
                            this,
                            "Some Unexpected error Occurred !!!",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }, Response.ErrorListener {
                    Toast.makeText(this, "Volley error occurred !!!", Toast.LENGTH_SHORT).show()

                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "81e89712a2ba04"
                        return headers
                    }
                }
            queue.add(jsonRequest)

        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->

                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()

            }
            dialog.setNegativeButton("Exit") { _, _ ->

                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }

    }

    private fun proceedToCart() {

        val gson = Gson()
        val foodItems = gson.toJson(orderList)

        val async = ItemsOfCart(this, restaurantId.toString(), foodItems, 1).execute()
        val result = async.get()
        if (result) {
            val data = Bundle()
            data.putInt("resId", restaurantId!!.toInt())
            data.putString("resName", restaurantName)
            val intent = Intent(this, MyCartActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Some unexpected error", Toast.LENGTH_SHORT)
                .show()
        }

    }

    class ItemsOfCart(
        context: Context,
        val restaurantId: String,
        val foodItems: String,
        val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()


        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.orderDao().insertOrder(OrderEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }

                2 -> {
                    db.orderDao().deleteOrder(OrderEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }
            }

            return false
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if(!RestaurantDetailsRecyclerAdapter.isCartEmpty){
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Confirmation")
            dialog.setMessage("Going back will clear the cart. Do you still want to proceed ?")
            dialog.setPositiveButton("Yes"){_, _ ->
                val intent = Intent(this,HomeActivity::class.java)
                startActivity(intent)
            }
            dialog.setNegativeButton("No"){_, _ ->

            }
            dialog.create()
            dialog.show()
        }else{
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
        }
    }
}

