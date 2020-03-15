package com.bublik.cheatcoupons.support

import android.app.Service
import android.os.Binder

class ServiceBinder<out T : Service>(val service: T) : Binder()