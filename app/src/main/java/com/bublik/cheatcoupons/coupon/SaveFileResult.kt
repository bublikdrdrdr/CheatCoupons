package com.bublik.cheatcoupons.coupon

data class SaveFileResult(
    val path: String? = null,
    val exceptionCode: ExceptionCode? = null,
    val exceptionData: String? = null
){
    enum class ExceptionCode{ FILES_EXIST, IO, OTHER}
}