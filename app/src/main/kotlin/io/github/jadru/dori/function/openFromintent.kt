package io.github.jadru.dori.function

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.webkit.WebView
import android.widget.Toast
import io.github.jadru.dori.R

fun openFromintent(webView: WebView, activity:Activity?, pref: SharedPreferences){
    val url_before = pref.getString("url_before", "http://www.google.com")
    val url = activity!!.intent.data
    if (url != null) {
        webView.loadUrl(url.toString())
        Toast.makeText(activity, activity!!.resources.getString(R.string.connectlink), Toast.LENGTH_SHORT).show()
    } else {
        webView.loadUrl(url_before)
    }
}