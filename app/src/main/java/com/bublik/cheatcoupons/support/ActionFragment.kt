package com.bublik.cheatcoupons.support

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
abstract class ActionFragment<in T> : Fragment() {

    private val defaultListener: FragmentActionListener<T> = object : FragmentActionListener<T> {
        override fun onFragmentAction(data: T) {
            Log.w(this.toString(), "Calling fragment action while not attached")
        }
    }

    private var listener: FragmentActionListener<T> = defaultListener

    @Suppress("UNCHECKED_CAST")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentActionListener<*>) {
            this.listener = context as FragmentActionListener<T>
        }else {
            throw IllegalArgumentException("Context must implement FragmentActionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = defaultListener
    }

    protected fun sendAction(data: T) {
        listener.onFragmentAction(data)
        listener.onFragmentAction(data, this)
    }
}