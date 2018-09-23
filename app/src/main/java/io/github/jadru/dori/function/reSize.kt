package io.github.jadru.dori.function

import android.support.constraint.ConstraintLayout
import android.view.View

fun reSizewithEdit(view: View){
    val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        params.setMargins(0, statusBarHeight, 0, navigationBarHeight + 55)
        view.layoutParams = params
}

fun reSizeAll(view: View){
    val params = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
    )
    params.setMargins(0, statusBarHeight, 0, navigationBarHeight)
    view.layoutParams = params
}