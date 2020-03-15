package com.bublik.cheatcoupons.data.mcdonalds

import android.util.Base64
import com.bublik.cheatcoupons.R
import com.bublik.cheatcoupons.data.AssetProvider
import com.bublik.cheatcoupons.data.CouponItem
import com.bublik.cheatcoupons.data.CouponType
import java.io.BufferedReader
import java.text.SimpleDateFormat
import java.util.*

object McDonaldsDataProvider {

    private const val hamburger: String = "mcdonalds_burger.png"
    private const val ice_cream: String = "mcdonalds_ice_cream.png"

    val mcDonaldsHamburger =
        CouponItem(
            "hamburger",
            R.string.hamburger,
            R.drawable.mcd_hamburger,
            CouponType.HTML_PDF,
            McDonaldsCouponProperties(),
            { c, a -> getContent(c as McDonaldsCouponProperties, hamburger, a) }
        )

    val mcDonaldsIceCream =
        CouponItem(
            "ice_cream",
            R.string.ice_cream,
            R.drawable.mcd_ice_cream,
            CouponType.HTML_PDF,
            McDonaldsCouponProperties(),
            { c, a -> getContent(c as McDonaldsCouponProperties, ice_cream, a) }
        )

    private val smallDateFormat = SimpleDateFormat("M/d/yyyy", Locale.ENGLISH)
    private val mainDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

    private fun getContent(
        properties: McDonaldsCouponProperties,
        imageAssetName: String,
        assetProvider: AssetProvider?
    ): String {
        val smallDate =
            smallDateFormat.format(properties[McDonaldsCouponProperties.Keys.date.first])
        val mainDate = mainDateFormat.format(properties[McDonaldsCouponProperties.Keys.date.first])
        return getTemplate(
            assetProvider!!,
            smallDate,
            mainDate,
            properties[McDonaldsCouponProperties.Keys.code.first],
            imageAssetName
        )
    }

    private fun getTemplate(
        assetProvider: AssetProvider,
        smallDate: String,
        mainDate: String,
        code: String,
        imageAssetName: String
    ) = BufferedReader(
        assetProvider.invoke("mcdonalds_coupon.html")
            .reader(Charsets.UTF_16)
    )
        .readText()
        .replace(formatParam("smallDate"), smallDate)
        .replace(formatParam("mainDate"), mainDate)
        .replace(formatParam("code"), code)
        .replace(formatParam("image"), readImageAsBase64(assetProvider, imageAssetName))

    private fun readImageAsBase64(assetProvider: AssetProvider, name: String): String {
        return Base64.encodeToString(assetProvider.invoke(name).readBytes(), Base64.DEFAULT)
    }

    private fun formatParam(name: String) = "\${$name}"
}