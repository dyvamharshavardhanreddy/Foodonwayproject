package com.trinath.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trinath.foodrunner.R
import com.trinath.foodrunner.model.RestaurantMenu
import java.util.ArrayList

class OrderItemHistoryRecyclerAdapter(val context: Context, val orderItemList: ArrayList<RestaurantMenu>): RecyclerView.Adapter<OrderItemHistoryRecyclerAdapter.OrderItemHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemHistoryViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_single_row, parent, false)
        return OrderItemHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderItemList.size
    }

    override fun onBindViewHolder(holder: OrderItemHistoryViewHolder, position: Int) {
        val order = orderItemList[position]
        holder.txtOrderMenuItem.text = order.itemName
        holder.txtOrderMenuPrice.text = order.itemCost
    }

    class OrderItemHistoryViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtOrderMenuItem: TextView = view.findViewById(R.id.txtOrderMenuItem)
        val txtOrderMenuPrice: TextView = view.findViewById(R.id.txtOrderMenuPrice)
    }
}