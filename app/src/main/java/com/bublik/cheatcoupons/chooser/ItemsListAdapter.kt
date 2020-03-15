package com.bublik.cheatcoupons.chooser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bublik.cheatcoupons.R
import com.bublik.cheatcoupons.data.Item
import kotlin.properties.Delegates

class ItemsListAdapter(private val clickListener: (Item) -> Unit) :
    RecyclerView.Adapter<ItemViewHolder>() {

    var items: List<Item> by Delegates.observable(emptyList()) { _, _, _ ->
        this.notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.coupon_chooser_item, parent, false)
            .let {
                return ItemViewHolder(it, clickListener)
            }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.item = items[position]
    }
}