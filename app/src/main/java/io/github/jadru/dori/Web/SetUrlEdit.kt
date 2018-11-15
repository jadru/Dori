package io.github.jadru.dori.web

import android.content.Context
import android.support.v4.content.ContextCompat.getSystemService
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.EditText
import kotlinx.android.synthetic.main.fragment_browser.*

fun SerUrlEdit(imm : InputMethodManager, webView : WebView, url_edit : EditText){

    webView.setOnScrollChangeListener { view, x, y, oldx, oldy -> run{
        if(y + 100 < oldy){
            url_edit.visibility = View.VISIBLE
        }
        if(y > oldy + 100){
            url_edit.visibility = View.INVISIBLE
        }
    } }

    url_edit.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            val ed = webView.url
            url_edit.setText(ed)
            imm!!.hideSoftInputFromWindow(url_edit.windowToken, 0)
        }
    }
}