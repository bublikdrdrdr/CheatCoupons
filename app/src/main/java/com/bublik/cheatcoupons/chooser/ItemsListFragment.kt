package com.bublik.cheatcoupons.chooser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bublik.cheatcoupons.R
import com.bublik.cheatcoupons.data.CouponsTreeProvider
import com.bublik.cheatcoupons.data.Item
import com.bublik.cheatcoupons.support.ActionFragment

class ItemsListFragment : ActionFragment<Item>() {

    private lateinit var adapter: ItemsListAdapter

    private val initialized: Boolean
        get() {
            return this::adapter.isInitialized
        }

    var code: String? = null
        set(value) {
            if (initialized) {
                field = value
                updateList()
            } else {
                lazyArguments.putString(CODE, value)
            }
        }

    var search: String? = null
        set(value) {
            if (initialized) {
                field = value
                updateList()
            } else {
                lazyArguments.putString(SEARCH, value)
            }
        }

    private val lazyArguments: Bundle
        get() {
            if (arguments == null) {
                arguments = Bundle()
            }
            return arguments!!
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        adapter = ItemsListAdapter(this::sendAction)
        arguments?.let {
            code = it.getString(CODE)
            search = it.getString(SEARCH)
        }

        updateList()

        return (inflater.inflate(
            R.layout.coupon_chooser_fragment,
            container,
            false
        ) as RecyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ItemsListFragment.adapter
        }
    }

    private fun updateList() {
        adapter.items = CouponsTreeProvider.getItems(code, search) { getString(it) }
    }

    companion object {
        private const val CODE = "CODE"
        private const val SEARCH = "SEARCH"

        fun create(code: String? = null, search: String? = null): ItemsListFragment {
            return ItemsListFragment().also {
                val arguments = Bundle()
                arguments.putString(CODE, code)
                arguments.putString(SEARCH, search)
                it.arguments = arguments
            }
        }
    }
}