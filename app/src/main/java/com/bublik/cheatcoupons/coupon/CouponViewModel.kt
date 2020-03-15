package com.bublik.cheatcoupons.coupon

import androidx.lifecycle.ViewModel
import com.bublik.cheatcoupons.data.CouponItem
import com.bublik.cheatcoupons.data.CouponProperties

class CouponViewModel : ViewModel() {

    lateinit var item: CouponItem<out CouponProperties, *>

    val initialized: Boolean
        get() = this::item.isInitialized
}