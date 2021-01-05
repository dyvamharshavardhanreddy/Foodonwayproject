package com.trinath.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trinath.foodrunner.R
import com.trinath.foodrunner.database.OrderEntity
import com.trinath.foodrunner.model.RestaurantMenu

class CartRecyclerAdapter (val context: Context, val orderList: ArrayList<RestaurantMenu>):
    RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_single_row, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val order = orderList[position]
        holder.txtOrderMenuItem.text = order.itemName
        holder.txtOrderMenuPrice.text = order.itemCost.toString()
    }

    class CartViewHolder(view: View):RecyclerView.ViewHolder(view){

        val txtOrderMenuItem:TextView = view.findViewById(R.id.txtOrderMenuItem)
        val txtOrderMenuPrice:TextView = view.findViewById(R.id.txtOrderMenuPrice)

    }
}