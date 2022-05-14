/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory.views


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventory.InventoryApplication
import com.example.inventory.R
import com.example.inventory.data.Item
import com.example.inventory.data.getFormattedPrice
import com.example.inventory.databinding.FragmentItemDetailBinding
import com.example.inventory.model.InventoryViewModel
import com.example.inventory.model.InventoryViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * [ItemDetailFragment] displays the details of the selected item.
 */
class ItemDetailFragment : Fragment() {
    private val navigationArgs: ItemDetailFragmentArgs by navArgs()

    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!

    // 01 - We will use this property to store information about a single entity.
    lateinit var item: Item

    // 02 - ViewModel: Use by delegate to hand off the property initialization to the activityViewModels class
    private val viewModel: InventoryViewModel by activityViewModels {

        // Pass in the InventoryViewModelFactory constructor.
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database.itemDao()
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 03 - This function takes an instance of the Item entity as the parameter and returns nothing.
    /**
     * Binds views with the passed in item data.
     */
    private fun bind(item: Item) {

        // apply{} scope function
        // This approach is similar to what we have done in the ItemListAdapter.kt file
        binding.apply {

            // Name
            itemName.text = item.itemName

            // Price: Formatted value
            itemPrice.text = item.getFormattedPrice()

            // Quantity: Converted to String
            itemCount.text = item.quantityInStock.toString()

            // The Sell button should be only enabled if the quantityInStock > 0
            sellItem.isEnabled = viewModel.isStockAvailable(item)

            // Execute the sellItem() through coroutine in InventoryViewModel.kt file
            sellItem.setOnClickListener { viewModel.sellItem(item) }

            // Delete: Confirmation Dialog - YES > deleteItem()
            deleteItem.setOnClickListener { showConfirmationDialog() }

            // FAB: Edit Button > editItem()
            editItem.setOnClickListener { editItem() }

        }

    }

    // 04 - We previously passed item id as a navigation argument to ItemDetailFragment from
    // the ItemListFragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        // navigationArgs:
        // Retrieve and assign the navigation argument to this new variable.
        val id = navigationArgs.itemId

        // retrieveItem:
        // Defined in the InventoryViewModel.kt, pass the id so the data can be retrieved.
        // Attach an observer to the returned value passing in the viewLifecycleOwner and a lambda.
        viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->

            // Pass in selectedItem as the parameter which contains the Item entity retrieved from the database
            item = selectedItem

            // Call bind() function passing in the item
            bind(item)

        }

    }

    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
     * YES: Initialize the deleteItem()
     * Calls deleteItem() function when the positive button is tapped
     */
    private fun showConfirmationDialog() {

        // Custom Confirmation Dialog
        MaterialAlertDialogBuilder(requireContext())

            // Title
            .setTitle(getString(android.R.string.dialog_alert_title))

            // Description
            .setMessage(getString(R.string.delete_question))

            // False:
            // 1. Sets whether this dialog is cancelable with the BACK key.
            // 2. User cannot candle the dialog by clicking on the blank area outside the dialog
            .setCancelable(false)

            // NO: Hide the confirmation dialog
            .setNegativeButton(getString(R.string.no)) { _, _ -> }

            // YES: Initialize the deleteItem() function
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteItem()
            }

            // Show the dialog
            .show()

    }

    /**
     * Deletes the current item and navigates to the list fragment.
     */
    private fun deleteItem() {

        // Initialize the deleteItem() on the viewModel, pass in the item
        // Note: the item instance contains the entity currently displayed on the Item Details screen.
        viewModel.deleteItem(item)

        // Attempts to navigate up in the navigation hierarchy
        // i.e. after deleting move back to the ItemListFragment
        findNavController().navigateUp()

    }

    /**
     * Navigate to the Edit item screen.
     */
    private fun editItem() {

        //
        val action = ItemDetailFragmentDirections.actionItemDetailFragmentToAddItemFragment(
            getString(R.string.edit_fragment_title),
            item.id
        )

        //
        this.findNavController().navigate(action)
    }

    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
