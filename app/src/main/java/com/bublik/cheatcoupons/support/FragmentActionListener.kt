package com.bublik.cheatcoupons.support

import androidx.fragment.app.Fragment

interface FragmentActionListener<in T> {

    fun onFragmentAction(data: T) {}

    fun onFragmentAction(data: T, fragment: Fragment) {}
}