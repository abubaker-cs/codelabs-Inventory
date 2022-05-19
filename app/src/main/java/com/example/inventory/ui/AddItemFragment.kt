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
package com.example.inventory.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventory.BaseApplication
import com.example.inventory.databinding.FragmentAddItemBinding
import com.example.inventory.model.Item
import com.example.inventory.ui.viewmodel.InventoryViewModel
import com.example.inventory.ui.viewmodel.InventoryViewModelFactory

/**
 * Fragment to add or update an item in the Inventory database.
 */
class AddItemFragment : Fragment() {

    private val navigationArgs: ItemDetailFragmentArgs by navArgs()

    //
    lateinit var item: Item

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    // to share the ViewModel across fragments.
    private val viewModel: InventoryViewModel by activityViewModels {

        // Use the database instance you created previously to call the itemDao constructor.
        InventoryViewModelFactory(
            (activity?.application as BaseApplication).database.itemDao()
        )

    }

    // Binding object instance corresponding to the fragment_add_item.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment
    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }


    /**
     * Binds views with the passed in [item] information.
     */
    private fun bind(item: Item) {

        // round the price to two decimal places using the format() function
        val price = "%.2f".format(item.itemPrice)

        //  Use the apply scope function on the binding property
        binding.apply {

            // Name
            itemName.setText(item.itemName, TextView.BufferType.SPANNABLE)

            // Price
            itemPrice.setText(price, TextView.BufferType.SPANNABLE)

            // Quantity
            itemCount.setText(item.quantityInStock.toString(), TextView.BufferType.SPANNABLE)

            // Button: Save > updateItem()
            saveAction.setOnClickListener { updateItem() }

        }

    }

    /**
     * This validation needs to be done in the ViewModel and not in the Fragment.
     *
     * Returns true if the EditTexts are not empty
     * We will use this function to verify user input before adding or updating the entity in the database.
     */
    private fun isEntryValid(): Boolean {

        // Return the value of the viewModel.isEntryValid() function
        return viewModel.isEntryValid(
            binding.itemName.text.toString(),
            binding.itemPrice.text.toString(),
            binding.itemCount.text.toString()
        )

    }

    /**
     * Inserts the new Item into database and navigates up to list fragment.
     */
    private fun addNewItem() {

        if (isEntryValid()) {

            viewModel.addNewItem(
                binding.itemName.text.toString(),
                binding.itemPrice.text.toString(),
                binding.itemCount.text.toString(),
            )

            // Prepare the action to navigate the user to the ListFragment view once the item will be saved
            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()

            // Initialize the navigation process.
            findNavController().navigate(action)

        }

    }

    /**
     * Updates an existing Item in the database and navigates up to list fragment.
     */
    private fun updateItem() {

        // Returns true if the EditTexts are not empty
        if (isEntryValid()) {

            viewModel.updateItem(

                // Item ID (Get from Navigation Args)
                this.navigationArgs.itemId,

                // Name
                this.binding.itemName.text.toString(),

                // Price
                this.binding.itemPrice.text.toString(),

                // Count (Quantity)
                this.binding.itemCount.text.toString()

            )

            // Prepare the action to navigate the user to the ListFragment view once the item will be saved
            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()

            // Initialize the navigation process.
            findNavController().navigate(action)

        }

    }

    /**
     * Called when the view is created.
     * The itemId Navigation argument determines the edit item  or add new item.
     * If the itemId is positive, this method retrieves the information from the database and
     * allows the user to update it.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        //  Retrieve itemId from the navigation arguments
        val id = navigationArgs.itemId

        // If the ID Exists
        if (id > 0) {

            // Retrieve the entity using the id and add an observer on it.
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->

                // Update the item property and call bind() passing in the item
                item = selectedItem
                bind(item)

            }

        } else {

            // Save > addNewItem()
            binding.saveAction.setOnClickListener {
                addNewItem()
            }

        }
    }


    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {

        super.onDestroyView()

        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)

        _binding = null

    }
}
