package com.bublik.cheatcoupons.support

import android.app.Service
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.ContextWrapper
import android.content.Intent
import android.content.ServiceConnection

fun Context.startService(serviceClass: Class<out Service>) {
    createIntent(serviceClass).also { startService(it) }
}

fun Context.createIntent(entity: Class<out ContextWrapper>) = Intent(this, entity)

fun <T : Service> Context.bindService(
    serviceClass: Class<T>,
    serviceSetter: (T?) -> Unit
): ServiceConnection {
    return ServiceConnector(serviceSetter).also {
        bindService(createIntent(serviceClass), it, BIND_AUTO_CREATE)
    }
}