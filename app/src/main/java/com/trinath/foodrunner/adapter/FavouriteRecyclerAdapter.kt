package com.trinath.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.trinath.foodrunner.R
import com.trinath.foodrunner.activity.RestaurantDetailsActivity
import com.trinath.foodrunner.database.RestaurantEntity
import com.trinath.foodrunner.fragments.HomeFragment

class FavouriteRecyclerAdapter(val context:Context,val restaurantList: List<RestaurantEntity>):RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val favRestaurant = restaurantList[position]


        holder.txtRestaurantName.text = favRestaurant.restaurantName
        holder.txtRestaurantRating.text = favRestaurant.restaurantRating
        holder.txtRestaurantPrice.text = favRestaurant.restaurantPrice
        Picasso.get().load(favRestaurant.restaurantImage).error(R.drawable.restaurent_default_img)
            .into(holder.imgRestaurantImage)


            holder.imgFavouritesEmpty.setImageResource(R.drawable.ic_favourites_fill)

        holder.imgFavouritesEmpty.setOnClickListener {
            val restaurantEntity = RestaurantEntity(
                favRestaurant.restaurantId.toInt(),
                favRestaurant.restaurantName,
                favRestaurant.restaurantRating,
                favRestaurant.restaurantPrice,
                favRestaurant.restaurantImage
            )

            if (HomeFragment.DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async = HomeFragment.DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result) {
                    holder.imgFavouritesEmpty.setImageResource(R.drawable.ic_favourites_empty)
                    Toast.makeText(context,"${favRestaurant.restaurantName} Removed from Favourites",
                        Toast.LENGTH_SHORT).show()
                }

            }
        }

        holder.homeLLContent.setOnClickListener {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra("id", favRestaurant.restaurantId.toString())
            intent.putExtra("name", favRestaurant.restaurantName)
            context.startActivity(intent)
        }


    }

    class  FavouriteViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtRestaurantRating)
        val txtRestaurantPrice: TextView = view.findViewById(R.id.txtPricePerPerson)
        val imgRestaurantImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val imgFavouritesEmpty: ImageView = view.findViewById(R.id.imgFavouritesEmpty)
        val homeLLContent: LinearLayout = view.findViewById(R.id.homeLLContent)

    }
}