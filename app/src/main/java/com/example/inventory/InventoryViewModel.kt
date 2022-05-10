package com.example.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch

// # 1
// ===
// Extend the InventoryViewModel class from the ViewModel class.
// Pass in the ItemDao object as a parameter to the default constructor.
class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {

    // Insert
    private fun insertItem(item: Item) {

        /**
         * Note:
         * The ViewModelScope is an extension property to the ViewModel class that automatically
         * cancels its child coroutines when the ViewModel is destroyed.
         */

        // Starting a coroutine in the ViewModelScope
        viewModelScope.launch {

            // Initialize the suspend function insert() on itemDao passing in the item
            itemDao.insert(item)

        }

    }

    // Get
    private fun getNewItemEntry(itemName: String, itemPrice: String, itemCount: String): Item {

        // Name, Price, Quantity
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )

    }

    // Add
    fun addNewItem(itemName: String, itemPrice: String, itemCount: String) {

        //
        val newItem = getNewItemEntry(itemName, itemPrice, itemCount)

        //
        insertItem(newItem)

    }

}


// # 2
// ===
// To instantiate the InventoryViewModel instance
// ==============================================
// 1. Pass in the same constructor parameter as the InventoryViewModel that is the ItemDao instance.
// 2. Extend the class from the ViewModelProvider.Factory class.
class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // Check if the modelClass is the same as the InventoryViewModel class
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {

            // Return an instance of it
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T

        }

        // Otherwise, throw an exception.
        throw IllegalArgumentException("Unknown ViewModel class")

    }

}