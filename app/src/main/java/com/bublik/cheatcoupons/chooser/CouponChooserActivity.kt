package com.bublik.cheatcoupons.chooser

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bublik.cheatcoupons.R
import com.bublik.cheatcoupons.coupon.CouponActivity
import com.bublik.cheatcoupons.data.Item
import com.bublik.cheatcoupons.support.FragmentActionListener

class CouponChooserActivity : FragmentActivity(R.layout.coupon_chooser_activity),
    FragmentActionListener<Item>, ToolbarActionsListener {

    companion object {
        private const val CATEGORIES_FRAGMENT_TAG = "categoriesFragment"
        private const val COUPONS_FRAGMENT_TAG = "couponsFragment"
    }

    private lateinit var model: CouponChooserViewModel
    private lateinit var toolbar: CouponChooserToolbar
    private lateinit var categoriesFragment: ItemsListFragment
    private lateinit var couponsFragment: ItemsListFragment

    private val currentFragment: ItemsListFragment
        get() {
            return if (model.isCategorySelected()) couponsFragment else categoriesFragment
        }

    private val currentFragmentTag: String
        get() {
            return if (model.isCategorySelected()) COUPONS_FRAGMENT_TAG else CATEGORIES_FRAGMENT_TAG
        }

    private val isSingleListLayout: Boolean
        get() {
            return findViewById<View>(R.id.listContainer) != null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(this)[CouponChooserViewModel::class.java]
        toolbar = findViewById(R.id.selectCouponToolbar)
        toolbar.title = getString(R.string.app_name)
        toolbar.listener = this
        setupFragments()
        updateState()
    }

    private fun updateState() {
        toolbar.searchText = model.searchText
        toolbar.category = model.selectedCategory
        if (isSingleListLayout) {
            setFragment(R.id.listContainer, currentFragment, currentFragmentTag)
            currentFragment.search = model.searchText
            currentFragment.code = model.selectedCategory?.code
        } else {
            setFragment(R.id.categoriesListFragment, categoriesFragment)
            setFragment(R.id.couponsListFragment, couponsFragment)
            categoriesFragment.search = model.categoriesSearchText
            couponsFragment.code = model.selectedCategory?.code
            couponsFragment.search = model.couponsSearchText
            findViewById<View>(R.id.couponsListFragment).visibility =
                getVisibility(model.isCategorySelected())
        }
    }

    private fun showCategories() {
        model.couponsSearchText = null
        model.selectedCategory = null
        updateState()
    }

    private fun onCategorySelected(item: Item) {
        if (model.categoriesSearchText == "") {
            model.categoriesSearchText = null
        }
        model.couponsSearchText = null
        model.selectedCategory = item
        updateState()
    }

    override fun onButtonClick(button: ToolbarActionsListener.ToolbarButton) {
        when (button) {
            ToolbarActionsListener.ToolbarButton.SEARCH -> setSearchModeEnabled(!toolbar.searchEnabled)
            ToolbarActionsListener.ToolbarButton.TITLE ->
                if (model.isCategorySelected()) {
                    onBackPressed()
                }
            ToolbarActionsListener.ToolbarButton.CATEGORY -> easterEgg()
        }
    }

    private fun setSearchModeEnabled(enabled: Boolean) {
        val text = if (enabled) "" else null
        model.searchText = text
        toolbar.searchText = text
    }

    override fun onSearchTextChanged(value: String) {
        model.searchText = value
        currentFragment.search = value
    }

    override fun onBackPressed() {
        if (model.isCategorySelected()) {
            showCategories()
        } else {
            super.onBackPressed()
        }
    }

    override fun onFragmentAction(data: Item, fragment: Fragment) {
        if (fragment == categoriesFragment) {
            onCategorySelected(data)
        } else if (fragment == couponsFragment) {
            Intent(this, CouponActivity::class.java).apply {
                this.putExtra(CouponActivity.COUPON_CODE, data.code)
                this.putExtra(CouponActivity.CATEGORY_CODE, this@CouponChooserActivity.model.selectedCategory!!.code)
            }.run {
                startActivity(this)
            }
        }
    }

    private fun setFragment(@IdRes container: Int, fragment: Fragment, tag: String? = null) {
        supportFragmentManager.beginTransaction()
            .replace(container, fragment, tag)
            .commit()
    }

    private fun getVisibility(visible: Boolean): Int = if (visible) View.VISIBLE else View.GONE

    private fun setupFragments() {
        if (isSingleListLayout) {
            categoriesFragment = setupFragment(CATEGORIES_FRAGMENT_TAG)
            couponsFragment = setupFragment(COUPONS_FRAGMENT_TAG)
        } else {
            categoriesFragment = setupFragment(R.id.categoriesListFragment)
            couponsFragment = setupFragment(R.id.couponsListFragment)
        }
    }

    private fun setupFragment(tag: String): ItemsListFragment {
        return (supportFragmentManager.findFragmentByTag(tag) as ItemsListFragment?)
            ?: ItemsListFragment()
    }

    private fun setupFragment(id: Int): ItemsListFragment {
        return (supportFragmentManager.findFragmentById(id) as ItemsListFragment?)
            ?: ItemsListFragment()
    }

    private var clicks = 0
    private var firstClick = System.currentTimeMillis()
    private fun easterEgg() {
        if (System.currentTimeMillis() - firstClick > 1000) {
            clicks = 0
            firstClick = System.currentTimeMillis()
        }
        clicks++
        if (clicks > 5) {
            Toast.makeText(this, R.string.easter_egg, Toast.LENGTH_LONG).show()
        }
    }
}