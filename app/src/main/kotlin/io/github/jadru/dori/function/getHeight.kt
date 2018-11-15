package io.github.jadru.dori.function

import android.app.Activity

val statusBarHeight: Int
    get() {
        var result = 0
        val resourceId = Activity().resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = Activity().resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

val navigationBarHeight: Int
    get() {
        var result = 0
        val resourceId = Activity().resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = Activity().resources.getDimensionPixelSize(resourceId)
        }
        return result
    }