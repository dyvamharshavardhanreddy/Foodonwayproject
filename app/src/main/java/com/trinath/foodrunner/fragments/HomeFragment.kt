package com.trinath.foodrunner.fragments


import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.trinath.foodrunner.R
import com.trinath.foodrunner.adapter.HomeRecyclerAdapter
import com.trinath.foodrunner.database.RestaurantDatabase
import com.trinath.foodrunner.database.RestaurantEntity
import com.trinath.foodrunner.model.Restaurants
import com.trinath.foodrunner.utill.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    val restaurantsInfoList = arrayListOf<Restaurants>()
    lateinit var progresslayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    var values = arrayOf(" Price(High to Low) ", " Price(Low to High) ", " Rating ")

    var ratingComparator = Comparator<Restaurants>{res1,res2->

        if(res1.restaurantRating.compareTo(res2.restaurantRating,true) == 0){
            res1.restaurantName.compareTo(res2.restaurantName,true)
        }else{
            res1.restaurantRating.compareTo(res2.restaurantRating,true)
        }
    }
    var priceComparator = Comparator<Restaurants>{res1,res2 ->

        if(res1.restaurantPrice.toString().compareTo(res2.restaurantPrice.toString(),true) == 0){
            res1.restaurantName.compareTo(res2.restaurantName,true)
        }else{
            res1.restaurantPrice.toString().compareTo(res2.restaurantPrice.toString(),true)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)

        progressBar = view.findViewById(R.id.progressbar)
        progresslayout = view.findViewById(R.id.progresslayout)

        progresslayout.visibility = View.VISIBLE

        setHasOptionsMenu(true)

        (context as AppCompatActivity).supportActionBar?.title = "All Restaurants"


        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"


        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url,
                null, Response.Listener {

                    try {
                        progresslayout.visibility = View.GONE

                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {

                            val dataArr = data.getJSONArray("data")
                            for (i in 0 until dataArr.length()) {
                                val restaurantJsonObject = dataArr.getJSONObject(i)
                                val restaurantObject = Restaurants(
                                    restaurantJsonObject.getString("id").toInt(),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one").toInt(),
                                    restaurantJsonObject.getString("image_url")
                                )

                                restaurantsInfoList.add(restaurantObject)

                                recyclerAdapter =
                                    HomeRecyclerAdapter(activity as Context, restaurantsInfoList)

                                recyclerHome.adapter = recyclerAdapter

                                recyclerHome.layoutManager = layoutManager

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

                    if (activity!=null){

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
        return view
    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        /*
           Mode 1 -> Check DB if the book is favourite or not
           Mode 2 -> Save the book into DB as favourite
           Mode 3 -> Remove the favourite book
           */

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants_db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {

                1 -> {
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(restaurantEntity.restaurantId.toString())
                    db.close()
                    return restaurant != null

                }
                2 -> {

                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true

                }
                3 -> {

                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true

                }
            }

            return false
        }


    }

    class GetAllFavAsyncTask(
        context: Context
    ) :
        AsyncTask<Void, Void, List<String>>() {

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants_db").build()

        override fun doInBackground(vararg params: Void?): List<String> {

            val list = db.restaurantDao().getAllRestaurants()
            val listOfIds = arrayListOf<String>()
            for (i in list) {
                listOfIds.add(i.restaurantId.toString())
            }
            return listOfIds
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_sort_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.getItemId()){
            R.id.btnSort->{
                createAlertDialogWithRadioButtonGroup()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun createAlertDialogWithRadioButtonGroup() {
        var checkedItem = -1
        val builder = AlertDialog.Builder(activity as Context)
        builder.setCancelable(true)
        builder.setSingleChoiceItems(values,checkedItem){_, i ->
            when(i) {
                0 -> {
                    Collections.sort(restaurantsInfoList, priceComparator)
                    restaurantsInfoList.reverse()
                    recyclerAdapter.notifyDataSetChanged()
                    checkedItem = 0
                }
                1 -> {
                    Collections.sort(restaurantsInfoList, priceComparator)
                    recyclerAdapter.notifyDataSetChanged()
                    checkedItem = 1
                }
                2 -> {
                    Collections.sort(restaurantsInfoList, ratingComparator)
                    restaurantsInfoList.reverse()
                    recyclerAdapter.notifyDataSetChanged()
                    checkedItem = 2
                }
            }

        }
        builder.setPositiveButton("Ok"){_,_->
            Toast.makeText(context,"Sorted",Toast.LENGTH_SHORT).show()
        }
        builder.create()
        builder.show()
    }
}
