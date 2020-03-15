package com.bublik.cheatcoupons.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

open class Item(
    val code: String,
    @StringRes val caption: Int,
    @DrawableRes val preview: Int? = null
)