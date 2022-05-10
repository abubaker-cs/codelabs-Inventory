package com.example.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.inventory.data.ItemDao

// # 1
// ===
// Extend the InventoryViewModel class from the ViewModel class.
// Pass in the ItemDao object as a parameter to the default constructor.
class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {


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