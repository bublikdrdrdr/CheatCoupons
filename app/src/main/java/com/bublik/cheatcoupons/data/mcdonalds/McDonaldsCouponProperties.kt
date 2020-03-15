package com.bublik.cheatcoupons.data.mcdonalds

import com.bublik.cheatcoupons.R
import com.bublik.cheatcoupons.data.CouponProperties
import java.util.*
import kotlin.random.Random

class McDonaldsCouponProperties : CouponProperties() {

    object Keys {
        val date = Pair("date", R.string.date)
        val code = Pair("code", R.string.code)
    }

    var date: Date = Date()
    var code: String = getDefault(Keys.code.first)

    override val propertyTypes: Map<Pair<String, Int>, PropertyType> = mapOf(
        Pair(Keys.date, PropertyType.DATE),
        Pair(Keys.code, PropertyType.STRING)
    )

    override fun <T> setProperty(key: String, value: T) {
        try {
            when (key) {
                Keys.code.first -> code = value as String
                Keys.date.first -> date = value as Date
                else -> throw IllegalArgumentException("Unknown key $key")
            }
        } catch (e: ClassCastException) {
            throw IllegalArgumentException("Bad value type (${value}) for property $key")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getProperty(key: String): T {
        return try {
            when (key) {
                Keys.code.first -> code as T
                Keys.date.first -> date as T
                else -> throw IllegalArgumentException("Unknown key $key")
            }
        } catch (e: ClassCastException) {
            throw IllegalArgumentException("Unexpected property type for key $key")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getDefault(key: String): T {
        return try {
            when (key) {
                Keys.date.first -> Date() as T
                Keys.code.first -> generateCouponUniqueCode() as T
                else -> throw UnsupportedOperationException("Unsupported key: $key")
            }
        } catch (e: ClassCastException) {
            throw IllegalArgumentException("Property type is not expected")
        }
    }

    private val randomUppercase: Char
        get() = randomChar('A', 'Z')

    private val randomDigit: Char
        get() = randomChar('0', '9')

    private fun generateCouponUniqueCode(): String {
        return "$randomUppercase-$randomDigit$randomDigit-$randomUppercase$randomUppercase-$randomDigit"
    }

    private fun randomChar(from: Char, to: Char): Char {
        return from + Random.nextInt(0, to.toInt() - from.toInt())
    }
}