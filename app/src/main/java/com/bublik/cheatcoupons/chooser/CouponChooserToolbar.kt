package com.bublik.cheatcoupons.chooser

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getSystemService
import com.bublik.cheatcoupons.R
import com.bublik.cheatcoupons.data.Item


class CouponChooserToolbar : Toolbar {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var titleButton: Button? = null
    private var pathDivider: ImageView? = null
    private var categoryButton: Button? = null
    private var searchField: EditText? = null
    private var searchButton: ImageButton? = null
    private var controls: View? = null


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        titleButton = findViewById(R.id.titleButton)
        pathDivider = findViewById(R.id.pathDivider)
        categoryButton = findViewById(R.id.categoryButton)
        searchField = findViewById(R.id.searchField)
        searchButton = findViewById(R.id.searchButton)
        controls = findViewById(R.id.controls)
        setListeners()
        refresh()
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        titleButton = null
        pathDivider = null
        categoryButton = null
        searchField = null
        searchButton = null
        listener = null
        controls = null
    }

    private fun refresh() {
        title = title
        category = category
        updateSearchModeEnabled()
    }

    private fun setListeners() {
        titleButton?.setOnClickListener { listener?.onButtonClick(ToolbarActionsListener.ToolbarButton.TITLE) }
        categoryButton?.setOnClickListener { listener?.onButtonClick(ToolbarActionsListener.ToolbarButton.CATEGORY) }
        searchButton?.setOnClickListener { listener?.onButtonClick(ToolbarActionsListener.ToolbarButton.SEARCH) }
        searchField?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (searchText != null) {
                    listener?.onSearchTextChanged(searchField?.text.toString())
                }
            }

        })
    }

    private fun updateSearchModeEnabled() {
        controls?.visibility = getVisibilityValue(!searchEnabled)
        searchField?.visibility = getVisibilityValue(searchEnabled)
        applyCategoryState(category)

        val imm: InputMethodManager? = getSystemService(context, InputMethodManager::class.java)
        if (searchEnabled) {
            if (searchField?.requestFocus() == true) {
                imm?.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT)
            }
        } else {
            imm?.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    private fun applyCategoryState(category: Item?) {
        val componentsVisibility = getVisibilityValue(category != null && !searchEnabled)
        pathDivider?.visibility = componentsVisibility
        categoryButton?.visibility = componentsVisibility
        categoryButton?.text = if (category != null) resources.getText(category.caption) else null
    }

    private fun getVisibilityValue(visible: Boolean): Int {
        return if (visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    var title: String = ""
        set(value) {
            field = value
            titleButton?.text = value
        }

    val searchEnabled: Boolean
        get() {
            return searchText != null
        }

    var category: Item? = null
        set(value) {
            field = value
            applyCategoryState(value)
        }

    var listener: ToolbarActionsListener? = null

    var searchText: String? = null
        set(value) {
            val updateState = (field == null) xor (value == null)
            field = value
            searchField?.setText(value)
            if (updateState) {
                updateSearchModeEnabled()
            }
        }

}