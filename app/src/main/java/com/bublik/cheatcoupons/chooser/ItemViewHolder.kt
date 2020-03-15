package com.bublik.cheatcoupons.chooser

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bublik.cheatcoupons.R
import com.bublik.cheatcoupons.data.Item

//TODO: try without private val
class ItemViewHolder(view: View, private val clickListener: (Item) -> Unit) :
    RecyclerView.ViewHolder(view) {

    private val captionView: TextView = view.findViewById(R.id.itemCaption)
    private val imageView: ImageView = view.findViewById(R.id.itemImage)

    init {
        view.setOnClickListener { clickListener.invoke(item) }
    }


    var item: Item = Item("example", android.R.string.untitled)
        set(value) {
            field = value
            setData(value)
        }

    private fun setData(item: Item) {
        captionView.text = itemView.context.getString(item.caption)
        imageView.setImageResource(item.preview ?: android.R.color.transparent)
    }
}