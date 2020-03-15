package com.bublik.cheatcoupons.chooser

interface ToolbarActionsListener {

    enum class ToolbarButton {
        TITLE, CATEGORY, SEARCH
    }

    fun onButtonClick(button: ToolbarButton)
    fun onSearchTextChanged(value: String)
}