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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventory.InventoryApplication
import com.example.inventory.R
import com.example.inventory.databinding.ItemListFragmentBinding
import com.example.inventory.ui.adapter.ItemListAdapter
import com.example.inventory.ui.viewmodel.InventoryViewModel
import com.example.inventory.ui.viewmodel.InventoryViewModelFactory

/**
 * Main fragment displaying details for all items in the database.
 */
class ItemListFragment : Fragment() {

    private var _binding: ItemListFragmentBinding? = null
    private val binding get() = _binding!!

    // 1. Declare a private immutable property called viewModel of the type InventoryViewModel
    // 2. Use by delegate to hand off the property initialization to the activityViewModels class.
    // 3. Pass in the InventoryViewModelFactory constructor.
    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database.itemDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ItemListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Declare a val named adapter
        // 2.  Initialize the new adapter property using the default constructor, ItemListAdapter{} passing in nothing.
        val adapter = ItemListAdapter {

            // This will navigate to teh fragment_item_detail.xml file
            // Assign the returned NavDirections object to action
            val action =
                ItemListFragmentDirections.actionItemListFragmentToItemDetailFragment(it.id)

            // 1. Retrieve a NavController instance using this.findNavController()
            // 2. call navigate() on it passing in the action
            this.findNavController().navigate(action)

        }

        // 3. Bind the newly created adapter to the recyclerView
        binding.recyclerView.adapter = adapter

        // 4. Attach an observer on the allItems to listen for the data changes.
        viewModel.allItems.observe(this.viewLifecycleOwner) { items ->

            // Pass in the new list
            items.let {

                // This will update the RecyclerView with the new items on the list.
                adapter.submitList(it)

            }

        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)

        // FAB onClick()
        binding.floatingActionButton.setOnClickListener {
            val action = ItemListFragmentDirections.actionItemListFragmentToAddItemFragment(
                getString(R.string.add_fragment_title)
            )
            this.findNavController().navigate(action)
        }

    }
}
