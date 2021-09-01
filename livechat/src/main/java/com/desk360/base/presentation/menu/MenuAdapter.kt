package com.desk360.base.presentation.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desk360.livechat.databinding.ItemMenuBinding

class MenuAdapter(
    private val delegate: CustomViewHolder.Delegate
) : RecyclerView.Adapter<MenuAdapter.CustomViewHolder>() {

    private val items = mutableListOf<MenuItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val item = this.items[position]
        holder.binding.run {
            imageViewMenu.setImageDrawable(item.icon)
            textViewTitle.text = item.title
            root.setOnClickListener { delegate.onMenuItemClick(item) }
        }
    }

    fun addCustomItem(customList: List<MenuItem>) {
        this.items.addAll(customList)
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    class CustomViewHolder(val binding: ItemMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        interface Delegate {
            fun onMenuItemClick(item: MenuItem)
        }
    }
}
