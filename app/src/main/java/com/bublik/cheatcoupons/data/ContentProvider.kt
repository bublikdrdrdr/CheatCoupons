package com.bublik.cheatcoupons.data

import java.io.InputStream

typealias AssetProvider = (String) -> InputStream
typealias ContentProvider<T> = (CouponProperties, AssetProvider?) -> T