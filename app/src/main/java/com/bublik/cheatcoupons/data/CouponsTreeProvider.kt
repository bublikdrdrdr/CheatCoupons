package com.bublik.cheatcoupons.data

import com.bublik.cheatcoupons.R
import com.bublik.cheatcoupons.data.mcdonalds.McDonaldsDataProvider

object CouponsTreeProvider {

    fun getItems(code: String?, search: String?, labelProvider: (Int) -> String): List<Item> {
        val items = if (code == null) categories else coupons(code)

        return items.filter {
            search.isNullOrEmpty() || labelProvider.invoke(it.caption).contains(search, true)
        }
    }

    private val categories: List<Item> =
        listOf(Item("mcdonalds", R.string.mcdonalds, R.drawable.mc_donalds))

    fun coupons(categoryCode: String): List<CouponItem<*, *>> {
        return when (categoryCode) {
            "mcdonalds" -> listOf(McDonaldsDataProvider.mcDonaldsHamburger, McDonaldsDataProvider.mcDonaldsIceCream)
            else -> throw UnsupportedOperationException("Unknown code $categoryCode")
        }
    }


}