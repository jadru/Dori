package io.github.jadru.dori.function

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Environment

internal var black: Boolean = false
internal var bgcolor: Int = 0

fun skinEngine(context: Context) {

    val pref: SharedPreferences = context.getSharedPreferences(context.packageName + "pref", 0)
    val expandbtncolor: Int
    val btnurlbg: Int
    val btnmenuimg: Int
    val btntabimg: Int
    val d: Drawable?
    val bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().path + "/Dori/themenow")
    if (bitmap == null) {
        d = null
    } else {
        d = BitmapDrawable(Activity().resources, bitmap)
    }
    black = pref.getBoolean("t-black", true)
    bgcolor = pref.getInt("t-color", Color.parseColor("#4dd0e1"))
    if (black) {
//        bg.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        bg.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE

    }
}