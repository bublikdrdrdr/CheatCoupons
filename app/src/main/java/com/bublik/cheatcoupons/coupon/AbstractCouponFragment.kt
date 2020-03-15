package com.bublik.cheatcoupons.coupon

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

abstract class AbstractCouponFragment : Fragment() {

    protected lateinit var model: CouponViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(requireActivity())[CouponViewModel::class.java]
    }
}