package com.bublik.cheatcoupons.chooser

import androidx.lifecycle.ViewModel
import com.bublik.cheatcoupons.data.Item

class CouponChooserViewModel : ViewModel() {

    var categoriesSearchText: String? = null
    var couponsSearchText: String? = null
    var selectedCategory: Item? = null

    var searchText: String?
        get() = if (selectedCategory == null) categoriesSearchText else couponsSearchText
        set(value) {
            if (selectedCategory == null) {
                categoriesSearchText = value
            } else {
                couponsSearchText = value
            }
        }

    fun isCategorySelected() = selectedCategory != null
}