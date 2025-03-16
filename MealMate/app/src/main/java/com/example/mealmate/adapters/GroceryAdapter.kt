package com.example.mealmate.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mealmate.data.GroceryItem
import com.example.mealmate.databinding.ItemGroceryBinding

class GroceryAdapter(
    private val onItemClick: (GroceryItem) -> Unit,
    private val onCheckboxClick: (GroceryItem, Boolean) -> Unit,
    private val onDeleteClick: (GroceryItem) -> Unit
) : ListAdapter<GroceryItem, GroceryAdapter.GroceryViewHolder>(GroceryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val binding = ItemGroceryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GroceryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GroceryViewHolder(
        private val binding: ItemGroceryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }

            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCheckboxClick(getItem(position), isChecked)
                }
            }

            binding.btnDelete.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(getItem(position))
                }
            }
        }

        fun bind(item: GroceryItem) {
            binding.apply {
                tvName.text = item.name
                tvQuantity.text = item.quantity
                checkBox.isChecked = item.purchased

                // Strike through text if item is purchased
                if (item.purchased) {
                    tvName.paintFlags = tvName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    tvQuantity.paintFlags = tvQuantity.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    tvName.paintFlags = tvName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    tvQuantity.paintFlags = tvQuantity.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            }
        }
    }

    private class GroceryDiffCallback : DiffUtil.ItemCallback<GroceryItem>() {
        override fun areItemsTheSame(oldItem: GroceryItem, newItem: GroceryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GroceryItem, newItem: GroceryItem): Boolean {
            return oldItem == newItem
        }
    }
}
