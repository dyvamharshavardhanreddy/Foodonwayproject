package com.trinath.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.squareup.picasso.Picasso
import com.trinath.foodrunner.R
import com.trinath.foodrunner.activity.RestaurantDetailsActivity
import com.trinath.foodrunner.database.RestaurantDatabase
import com.trinath.foodrunner.database.RestaurantEntity
import com.trinath.foodrunner.fragments.HomeFragment
import com.trinath.foodrunner.model.Restaurants
import kotlinx.android.synthetic.main.recycler_restaurant_details_single_row.view.*
import java.util.ArrayList

class HomeRecyclerAdapter(val context: Context, val itemList: ArrayList<Restaurants>): RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeRecyclerAdapter.HomeViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeRecyclerAdapter.HomeViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtRestaurantRating.text = restaurant.restaurantRating
        holder.txtRestaurantPrice.text = restaurant.restaurantPrice.toString()
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.restaurent_default_img)
            .into(holder.imgRestaurantImage)

        val listOfFavourites = HomeFragment.GetAllFavAsyncTask(context).execute().get()


        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(restaurant.restaurantId.toString())) {
            holder.imgFavouritesEmpty.setImageResource(R.drawable.ic_favourites_fill)
        } else {
            holder.imgFavouritesEmpty.setImageResource(R.drawable.ic_favourites_empty)
        }

        holder.imgFavouritesEmpty.setOnClickListener {
            val restaurantEntity = RestaurantEntity(
                restaurant.restaurantId,
                restaurant.restaurantName,
                restaurant.restaurantRating,
                restaurant.restaurantPrice.toString(),
                restaurant.restaurantImage
            )

            if (!HomeFragment.DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async =
                    HomeFragment.DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.imgFavouritesEmpty.setImageResource(R.drawable.ic_favourites_fill)
                    Toast.makeText(context,"${restaurant.restaurantName} added to Favourites",Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = HomeFragment.DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result) {
                    holder.imgFavouritesEmpty.setImageResource(R.drawable.ic_favourites_empty)
                    Toast.makeText(context,"${restaurant.restaurantName} Removed from Favourites",Toast.LENGTH_SHORT).show()
                }
            }
        }

        holder.homeLLContent.setOnClickListener {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra("id", restaurant.restaurantId.toString())
            intent.putExtra("name", restaurant.restaurantName)
            context.startActivity(intent)
        }

    }

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtRestaurantRating)
        val txtRestaurantPrice: TextView = view.findViewById(R.id.txtPricePerPerson)
        val imgRestaurantImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val imgFavouritesEmpty: ImageView = view.findViewById(R.id.imgFavouritesEmpty)
        val homeLLContent: LinearLayout = view.findViewById(R.id.homeLLContent)
    }
}