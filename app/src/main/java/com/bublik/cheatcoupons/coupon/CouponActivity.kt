package com.bublik.cheatcoupons.coupon

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bublik.cheatcoupons.R
import com.bublik.cheatcoupons.data.CouponItem
import com.bublik.cheatcoupons.data.CouponType
import com.bublik.cheatcoupons.data.CouponsTreeProvider

class CouponActivity : FragmentActivity(R.layout.coupon_activity) {

    companion object {
        const val COUPON_CODE = "code"
        const val CATEGORY_CODE = "category"
    }

    private lateinit var model: CouponViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(this)[CouponViewModel::class.java]
        if (!model.initialized) {
            model.item = findItem()?:throw IllegalArgumentException("Item not found")
        }

        val fragment = getFragmentForCoupon(model.item)
        supportFragmentManager.beginTransaction()
            .replace(R.id.couponContainer, fragment)
            .commit()
    }

    private fun findItem(): CouponItem<*, *>? {
        return intent.getStringExtra(CATEGORY_CODE)?.let { category ->
            intent.getStringExtra(COUPON_CODE)?.let { coupon ->
                CouponsTreeProvider.coupons(category).find { i -> i.code == coupon }
            }
        }
    }

    private fun getFragmentForCoupon(coupon: CouponItem<*, *>): AbstractCouponFragment {
        return when (coupon.couponType) {
            CouponType.HTML_PDF -> PdfViewerCouponFragment()
        }
    }
}