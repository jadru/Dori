package io.github.jadru.dori.function

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.webkit.WebView

fun saveUrl(activity: Activity, webView: WebView){
    val pref: SharedPreferences = activity.getSharedPreferences(activity.packageName + "pref", 0)
    val statusbarcolor = 0
    val editor = pref.edit()
    val url_before = webView.url
    editor.putString("url_before", url_before)
    editor.putInt("status_before", statusbarcolor)
    editor.apply()
}