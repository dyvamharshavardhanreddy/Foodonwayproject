package com.trinath.foodrunner.adapter

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings.System.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trinath.foodrunner.R
import com.trinath.foodrunner.activity.RestaurantDetailsActivity
import com.trinath.foodrunner.database.OrderEntity
import com.trinath.foodrunner.fragments.HomeFragment
import com.trinath.foodrunner.model.RestaurantMenu
import java.util.ArrayList

class RestaurantDetailsRecyclerAdapter(
    val context: Context,
    private val menuList: ArrayList<RestaurantMenu>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<RestaurantDetailsRecyclerAdapter.MenuViewHolder>() {

    companion object {
        var isCartEmpty = true
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MenuViewHolder {
        val itemView = LayoutInflater.from(p0.context)
            .inflate(R.layout.recycler_restaurant_details_single_row, p0, false)

        return MenuViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    interface OnItemClickListener {
        fun onAddItemClick(foodItem: RestaurantMenu)
        fun onRemoveItemClick(foodItem: RestaurantMenu)
    }


    override fun onBindViewHolder(p0: MenuViewHolder, p1: Int) {
        val menuObject = menuList[p1]
        p0.foodItemName.text = menuObject.itemName
        val cost = "Rs. ${menuObject.itemCost}"
        p0.foodItemCost.text = cost
        p0.sno.text = (p1 + 1).toString()
        p0.addToCart.setOnClickListener {
            p0.addToCart.visibility = View.GONE
            p0.removeFromCart.visibility = View.VISIBLE
            listener.onAddItemClick(menuObject)
        }

        p0.removeFromCart.setOnClickListener {
            p0.removeFromCart.visibility = View.GONE
            p0.addToCart.visibility = View.VISIBLE
            listener.onRemoveItemClick(menuObject)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foodItemName: TextView = view.findViewById(R.id.txtMenuItemName)
        val foodItemCost: TextView = view.findViewById(R.id.txtMenuItemCost)
        val sno: TextView = view.findViewById(R.id.txtMenuItemCount)
        val addToCart: Button = view.findViewById(R.id.btnAdd)
        val removeFromCart: Button = view.findViewById(R.id.btnRemove)
    }
}
