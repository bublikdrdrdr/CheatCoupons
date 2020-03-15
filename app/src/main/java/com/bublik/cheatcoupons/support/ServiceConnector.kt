package com.bublik.cheatcoupons.support
import android.app.Service
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder

class ServiceConnector<out T : Service>(private val serviceListener: (T?) -> Unit) :
    ServiceConnection {
    override fun onServiceDisconnected(name: ComponentName?) {
        serviceListener(null)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        val serviceBinder = binder as ServiceBinder<*>
        val service: T = serviceBinder.service as T
        serviceListener(service)
    }

}
