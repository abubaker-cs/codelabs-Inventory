package com.example.inventory.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.NumberFormat

@Entity(tableName = "item")
data class Item(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val itemName: String,

    @ColumnInfo(name = "price")
    val itemPrice: Double,

    @ColumnInfo(name = "quantity")
    val quantityInStock: Int

)

// Extension Function
/**
 * Kotlin provides an ability to extend a class with new functionality without having to inherit
 * from the class or modify the existing definition of the class. That means you can add functions
 * to an existing class without having to access its source code.
 *
 * For example, you can write new functions for a class from a third-party library that you can't
 * modify. Such functions are available for calling in the usual way, as if they were methods of the
 * original class. These functions are called extension functions.
 */
fun Item.getFormattedPrice(): String = NumberFormat.getCurrencyInstance().format(itemPrice)