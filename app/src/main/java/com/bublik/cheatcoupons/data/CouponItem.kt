package com.bublik.cheatcoupons.data

class CouponItem<P: CouponProperties, T>(
    code: String, caption: Int, preview: Int?,
    val couponType: CouponType,
    val properties: P,
    private val contentProvider: ContentProvider<T>
) : Item(code, caption, preview){

    fun getContent(assetsProvider: AssetProvider? = null): T {
        return contentProvider.invoke(properties, assetsProvider)
    }
}