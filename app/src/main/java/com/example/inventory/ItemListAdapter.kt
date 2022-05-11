package com.example.inventory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.data.Item
import com.example.inventory.data.getFormattedPrice
import com.example.inventory.databinding.ItemListItemBinding

/**
 * [ListAdapter] implementation for the recyclerview.
 */

class ItemListAdapter(private val onItemClicked: (Item) -> Unit) :
    ListAdapter<Item, ItemListAdapter.ItemViewHolder>(DiffCallback) {

    // returns a new ViewHolder when RecyclerView needs one.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        // Inflate the item_list_item.xml file
        return ItemViewHolder(
            ItemListItemBinding.inflate(LayoutInflater.from(parent.context))
        )

    }

    //
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        // Get the current item using the method getItem(), passing the position.
        val current = getItem(position)

        // Click Event
        holder.itemView.setOnClickListener {

            //
            onItemClicked(current)

        }

        holder.bind(current)

    }

    // Extend ItemViewHolder from RecyclerView.ViewHolder.
    class ItemViewHolder(private var binding: ItemListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Override the bind() function, pass in the Item object.
        fun bind(item: Item) {

            binding.apply {

                // Name
                itemName.text = item.itemName

                // Price
                itemPrice.text = item.getFormattedPrice()

                // Quantity
                itemQuantity.text = item.quantityInStock.toString()

            }

        }

    }

    //
    companion object {

        // Add the constructor parameter DiffCallback; the ListAdapter will use this to figure out what changed in the list.
        private val DiffCallback = object : DiffUtil.ItemCallback<Item>() {

            // Same Items?
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem === newItem
            }

            // Same Content?
            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.itemName == newItem.itemName
            }
        }

    }

}