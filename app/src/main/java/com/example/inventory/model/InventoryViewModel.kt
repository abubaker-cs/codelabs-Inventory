package com.example.inventory

import androidx.lifecycle.*
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch

// # 1
// ===
// Extend the InventoryViewModel class from the ViewModel class.
// Pass in the ItemDao object as a parameter to the default constructor.
class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {

    // This will be used to retrieve items from the database.
    // 1. The getItems() function returns a Flow
    // 2. To consume the data as a LiveData value, use the asLiveData() function
    val allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()

    // It will be used in ItemDetailFragment.kt file to retrieve details of the selected item.
    /**
     * Retrieve an item from the repository.
     */
    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.getItem(id).asLiveData()
    }


    // =============================================================== # 1 - Initializer
    // Add: This will be called from the UI fragment to add Item details to the database.
    /**
     * Inserts the new Item into database.
     */
    fun addNewItem(itemName: String, itemPrice: String, itemCount: String) {

        // Pass in item detail strings to getNewItemEntry() function and assign the returned value
        // to a val named newItem
        val newItem = getNewItemEntry(itemName, itemPrice, itemCount)

        // Add the new entity to the database
        insertItem(newItem)

    }

    // =============================================================== # 2 - Prepare Item Instance
    // Prepare the Item Instance
    // =========================
    // This function takes in three strings and returns >>> an Item instance:
    // 1. Name
    // 2. Item Price
    // 3. Count (Quantity)
    /**
     * Returns an instance of the [Item] entity class with the item info entered by the user.
     * This will be used to add a new entry to the Inventory database.
     */
    private fun getNewItemEntry(itemName: String, itemPrice: String, itemCount: String): Item {

        // Name, Price, Quantity
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )

    }


    // =============================================================== # 3 - Insert using coroutine
    /**
     * Launching a new coroutine to insert an item in a non-blocking way
     */
    private fun insertItem(item: Item) {

        /**
         * Note:
         * The ViewModelScope is an extension property to the ViewModel class that automatically
         * cancels its child coroutines when the ViewModel is destroyed.
         *
         * Suspend functions are only allowed to be called from a coroutine or another suspend function
         */

        // Starting a coroutine in the ViewModelScope
        viewModelScope.launch {

            // Initialize the suspend function insert() on itemDao passing in the item
            itemDao.insert(item)

        }

    }

    // =============================================================== # * - updateItem
    /**
     * Launching a new coroutine to update an item in a non-blocking way
     */
    private fun updateItem(item: Item) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    // =============================================================== # * - sellItem
    /**
     * Decreases the stock by one unit and updates the database.
     */
    fun sellItem(item: Item) {
        if (item.quantityInStock > 0) {

            // Decrease the quantity by 1
            val newItem = item.copy(quantityInStock = item.quantityInStock - 1)

            //
            updateItem(newItem)

        }
    }

    // =============================================================== # * - deleteItem
    /**
     * Launching a new coroutine to delete an item in a non-blocking way
     */
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    // =============================================================== # * - getUpdatedItemEntry
    /**
     * Called to update an existing entry in the Inventory database.
     * Returns an instance of the [Item] entity class with the item info updated by the user.
     */
    private fun getUpdatedItemEntry(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ): Item {
        return Item(
            id = itemId,
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
    }

    // =============================================================== # * - updateItem
    /**
     * Updates an existing Item in the database.
     */
    fun updateItem(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ) {
        val updatedItem = getUpdatedItemEntry(itemId, itemName, itemPrice, itemCount)
        updateItem(updatedItem)
    }

    // =============================================================== # * - isStockAvailable.
    /**
     * Returns true if stock is available to sell, false otherwise.
     */
    fun isStockAvailable(item: Item): Boolean {
        return (item.quantityInStock > 0)
    }

    // =============================================================== # * - Validation
    /**
     * Returns true if the EditTexts are not empty
     */
    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean {

        // Return FALSE if any EditText field is empty
        if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank()) {
            return false
        }

        // Returns true if the EditTexts are not empty
        return true

    }

}


// # 2
// ===
// To instantiate the InventoryViewModel instance
// ==============================================
// 1. Pass in the same constructor parameter as the InventoryViewModel that is the ItemDao instance.
// 2. Extend the class from the ViewModelProvider.Factory class.
/**
 * Factory class to instantiate the [ViewModel] instance.
 */
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