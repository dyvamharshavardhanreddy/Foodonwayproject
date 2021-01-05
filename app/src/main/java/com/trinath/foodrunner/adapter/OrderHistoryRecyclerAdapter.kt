package com.trinath.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trinath.foodrunner.R
import com.trinath.foodrunner.model.OrderDetails
import com.trinath.foodrunner.model.RestaurantMenu
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList



class OrderHistoryRecyclerAdapter(val context: Context, val orderList: ArrayList<OrderDetails>): RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderHistoryViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_order_history_single_row, parent, false)
        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder:OrderHistoryViewHolder, position: Int) {
        val order = orderList[position]
        holder.txtOrderRestaurantName.text = order.restaurantName
        holder.txtOrderDate.text = formatDate(order.orderPlacedAt)
        setUpRecycler(holder.recyclerOrderHistory,order)
    }

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val txtOrderRestaurantName:TextView = view.findViewById(R.id.txtOrderRestaurantName)
        val txtOrderDate:TextView = view.findViewById(R.id.txtOrderDate)
        val recyclerOrderHistory:RecyclerView = view.findViewById(R.id.recyclerOrderHistory)

    }

    fun formatDate(date:String): String {
        val receivedDate = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH).parse(date)
        if (receivedDate!=null) {
            return SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(receivedDate)
        }
        throw TypeCastException("null cannot be cast to non-null type java.util.Date")
    }

    private fun setUpRecycler(recyclerView: RecyclerView, order: OrderDetails) {
        val listOfOrders = ArrayList<RestaurantMenu>()
        val n: Int = order.foodItems.length()
        for (i in 0 until n) {
            val jSONObject: JSONObject = order.foodItems.getJSONObject(i)
            val restaurantMenu = RestaurantMenu(
                jSONObject.getString("food_item_id"),
                jSONObject.getString("name"),
                jSONObject.getString("cost")
            )
            listOfOrders.add(restaurantMenu)
            val recyclerAdapter = OrderItemHistoryRecyclerAdapter(context, listOfOrders)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.adapter = recyclerAdapter
        }
    }

}