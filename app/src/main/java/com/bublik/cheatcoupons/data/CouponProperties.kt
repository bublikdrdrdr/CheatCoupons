package com.bublik.cheatcoupons.data

abstract class CouponProperties {

    enum class PropertyType { STRING, DATE }

    abstract val propertyTypes: Map<Pair<String, Int>, PropertyType>

    protected abstract fun <T> setProperty(key: String, value: T)
    protected abstract fun <T> getProperty(key: String): T

    operator fun <T> get(key: String): T = getProperty(key)
    operator fun <T> set(key: String, value: T) = setProperty(key, value)

    abstract fun <T> getDefault(key: String): T
}