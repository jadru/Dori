package io.github.jadru.dori.function

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import io.github.jadru.dori.web.PREFS_FILENAME

fun saveUrl(webView: WebView, pref: SharedPreferences){
    val statusbarcolor = 0
    val editor = pref.edit()
    val url_before = webView.url
    editor.putString("url_before", url_before)
    editor.putInt("status_before", statusbarcolor)
    editor.apply()
}