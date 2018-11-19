package io.github.jadru.dori.web

import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.EditText
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_browser.*

fun SetUrlEdit(webView : WebView, url_edit : EditText, context: Context?, activity: Activity?){

    val pref = context!!.getSharedPreferences(PREFS_FILENAME, AppCompatActivity.MODE_PRIVATE)
    val searchengineurl: String? = pref!!.getString("searchengine", "https://www.google.co.kr/search?q=")
    val imm: InputMethodManager? = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    url_edit.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            val ed = webView.url
            url_edit.setText(ed)
            imm!!.hideSoftInputFromWindow(url_edit.windowToken, 0)
        }
    }

    url_edit.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
        // TODO Auto-generated method stub
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                var strUrl = url_edit.text.toString()
                if (strUrl.startsWith("http")) {
                    webView.loadUrl(strUrl)
                } else if (strUrl.contains(".")) {
                    strUrl = "http://$strUrl"
                    webView.loadUrl(strUrl)
                } else {
                    strUrl = searchengineurl + strUrl
                    webView.loadUrl(strUrl)
                }
                imm!!.hideSoftInputFromWindow(url_edit.windowToken, 0)
                return@OnKeyListener true
            }
        }
        false
    })
}