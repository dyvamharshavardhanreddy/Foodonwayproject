package com.trinath.foodrunner.fragments


import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.trinath.foodrunner.R
import com.trinath.foodrunner.adapter.FavouriteRecyclerAdapter
import com.trinath.foodrunner.database.RestaurantDatabase
import com.trinath.foodrunner.database.RestaurantEntity

/**
 * A simple [Fragment] subclass.
 */
class FavouritesFragment : Fragment() {

    lateinit var recyclerFavourite:RecyclerView
    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar:ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    lateinit var noItemLayout:RelativeLayout
    lateinit var noItem:TextView

    var dbRestaurantList = listOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        (context as AppCompatActivity).supportActionBar?.title = "Favourite Restaurants"

        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        noItemLayout = view.findViewById(R.id.noItemLayout)
        noItem = view.findViewById(R.id.noItem)

        progressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(activity)

        dbRestaurantList = RetrieveFavourites(activity as Context).execute().get()


        noItemLayout.visibility = View.GONE
        noItem.visibility = View.GONE

        if(activity != null){
            progressLayout.visibility = View.GONE
            progressBar.visibility = View.GONE
            if (dbRestaurantList.isEmpty()){
                noItemLayout.visibility = View.VISIBLE
                noItem.visibility = View.VISIBLE
            }
            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context,dbRestaurantList)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager

        }



        return  view
    }

    class RetrieveFavourites(val context: Context): AsyncTask<Void, Void,List<RestaurantEntity>>(){
        override fun doInBackground(vararg p0: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context,RestaurantDatabase::class.java,"restaurants_db").build()


            return db.restaurantDao().getAllRestaurants()
        }

    }

}
