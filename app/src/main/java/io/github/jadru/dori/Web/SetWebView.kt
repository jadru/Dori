package io.github.jadru.dori.web

import android.content.Context
import android.net.Uri
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import io.github.jadru.dori.activity.MainActivity
import android.webkit.JavascriptInterface



var mUploadMsg: ValueCallback<Uri>? = null
val RC_FILE_CHOOSE = 1

fun setWebView(webView: WebView, progressBar: ProgressBar) {

    val webSettings = webView.settings  // 웹세팅 객체 생성
    webSettings.mediaPlaybackRequiresUserGesture = true
    webSettings.setSupportZoom(true)
    webSettings.builtInZoomControls = true // 줌컨트롤 활성화
    webSettings.displayZoomControls = false
    webSettings.domStorageEnabled = true
    webSettings.setSupportMultipleWindows(true)
    webSettings.pluginState = WebSettings.PluginState.ON
    webSettings.javaScriptCanOpenWindowsAutomatically = true
    webSettings.useWideViewPort = true
    webSettings.loadWithOverviewMode = true
    webSettings.loadsImagesAutomatically = true
    webSettings.javaScriptEnabled = true  // 자바스크립트 활성화
    webView.isHapticFeedbackEnabled = true
    webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    webView.addJavascriptInterface(JsInterface(), "getcolor")
    webView.isScrollbarFadingEnabled = true
    webView.scrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY
    webView.isVerticalScrollBarEnabled = true
    webView.isHorizontalScrollBarEnabled = false
//        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 5.1.1; " + deviceName + "Build/LMY48B; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/43.0.2357.65 Mobile Safari/537.36")

    webView.requestFocus()

    WebView.setWebContentsDebuggingEnabled(true)

    webView.webChromeClient = ChromeClient(MainActivity(), progressBar)

    DownloadService(webView)

}