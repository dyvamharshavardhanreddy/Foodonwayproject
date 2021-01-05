package com.trinath.foodrunner.model

import org.json.JSONArray

data class OrderDetails(
    val orderId:String,
    val restaurantName:String,
    val totalCost:String,
    val orderPlacedAt:String,
    val foodItems:JSONArray
)