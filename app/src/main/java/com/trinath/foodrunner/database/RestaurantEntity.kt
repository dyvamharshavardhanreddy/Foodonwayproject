package com.trinath.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trinath.foodrunner.model.RestaurantMenu


@Entity(tableName = "Restaurants")
data class RestaurantEntity(
    @PrimaryKey val restaurantId:Int,
    @ColumnInfo(name = "restaurant_name") val restaurantName:String,
    @ColumnInfo(name = "restaurant_rating") val restaurantRating:String,
    @ColumnInfo(name = "restaurant_price") val restaurantPrice:String,
    @ColumnInfo(name = "restaurant_image") val restaurantImage:String

)

