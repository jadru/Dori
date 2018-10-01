package io.github.jadru.dori.web

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.*
import android.content.ClipData.newPlainText
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.EditText
import android.widget.ProgressBar
import io.github.jadru.dori.R
import io.github.jadru.dori.function.saveUrl

class WebBrowserClient(val activity: Activity, val webView: WebView,
                       var progressBar: ProgressBar, var url_edit: EditText, var homepagelink: String) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        var url = request!!.url.toString()
        if (url.startsWith("http")) {
            webView.loadUrl(url)
        } else if (url.startsWith("copy")) {
            val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.primaryClip = ClipData.newPlainText("Copied link by Dori", url)
        } else if (url.startsWith("sms")) {
            if (url.split(("=").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size > 1) {
                url = url.split(("=").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            }
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("sms:")
            intent.putExtra("sms_body", url)
            activity.startActivity(intent)
        } else if (url.startsWith("kakolink:")||
                url.startsWith("mailto:")||
                url.startsWith("tel")) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            activity.startActivity(intent)
        } else if (url.contains("play.google.com")) {
            // play.google.com 도메인이면서 App 링크인 경우에는 market:// 로 변경
            val params = url.split(("details").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (params.size > 1) {
                val uurl = "market://details" + params[1]
                webView.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uurl)))
            }
        } else {
            whenError()
        }
        url_edit.text = Editable.Factory.getInstance().newEditable(url)
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        val pref: SharedPreferences = activity.getSharedPreferences(activity.packageName + "pref", 0)
        val sslagree = pref.getBoolean("sslagree", false)
        if (sslagree) {
            val alt_bld = AlertDialog.Builder(activity)
            alt_bld.setMessage(activity.resources.getString(R.string.ssltitle)).setCancelable(
                    false)
                    .setPositiveButton(activity.resources.getString(R.string.btn_no)
                    ) { dialog, id ->
                        handler.cancel()
                        webView.goBack()
                    }
                    .setNegativeButton(activity.resources.getString(R.string.btn_ok),
                            object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface, id: Int) {
                                    handler.proceed()  //SSL 에러가 발생해도 계속 진행!
                                    Snackbar.make(webView, activity.resources.getString(R.string.recommendout), Snackbar.LENGTH_LONG)
                                            .setAction(activity.resources.getString(R.string.back), object : View.OnClickListener {
                                                override fun onClick(v: View) {
                                                    webView.goBack()
                                                }
                                            }).show()
                                    dialog.cancel()
                                }
                            })
            val alert = alt_bld.create()
            // Title for AlertDialog
            alert.setTitle("SSL Error")
            alert.setCanceledOnTouchOutside(false)
            // Icon f
            alert.show()
        } else {
            val alt_bld = AlertDialog.Builder(activity)
            alt_bld.setMessage(activity.resources.getString(R.string.ssltitle)).setCancelable(
                    false)
                    .setPositiveButton(activity.resources.getString(R.string.back),
                            object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface, id: Int) {
                                    webView.goBack()
                                }
                            })
            val alert = alt_bld.create()
            // Title for AlertDialog
            alert.setTitle("SSL Error")
            alert.setCanceledOnTouchOutside(false)
            // Icon f
            alert.show()
        }
    }

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {  // 페이지로딩이 시작되면
        super.onPageStarted(view, url, favicon)
        progressBar.visibility = View.VISIBLE  // 프로그레스바 보이기
        if (url_edit.isFocused) {
            url_edit.clearFocus()
        }
        url_edit.text = Editable.Factory.getInstance().newEditable(url)
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        progressBar.visibility = View.INVISIBLE
        url_edit.visibility = View.INVISIBLE
        webView.isScrollContainer = true
        webView.loadUrl("javascript:window.getcolor.processHTML( (function (){var metas = document.getElementsByTagName('meta'); \n" +
                "\n" +
                "   for (var i=0; i<metas.length; i++) { \n" +
                "      if (metas[i].getAttribute(\"name\") == \"description\") { \n" +
                "         return metas[i].getAttribute(\"content\"); \n" +
                "      } \n" +
                "   } \n" +
                "\n" +
                "    return \"\";})() );");
        if (!(url_edit.isFocused)) {
            val ed = webView.url
            url_edit.setText(ed)
        }
        CookieSyncManager.getInstance().sync()
        saveUrl(activity, webView)

    }
    override fun onLoadResource(view: WebView?, url: String?){
        // 이미지 같은 리소스 로드 시
    }

    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean){
//        webView.loadUrl(url)
        url_edit.setText(url)
        saveUrl(activity, webView)
        // 방문한 링크 업데이트 시
    }

    override fun onFormResubmission(view: WebView, dontResend: Message, resend: Message) {
        resend.sendToTarget()
        super.onFormResubmission(view, dontResend, resend)
        // POST 데이터가 포함된 페이지에서 POST 데이터를 다시 보내려고 하는 경우에 사용
    }
    fun whenError() {
        webView.loadUrl("file:///android_asset/error.html")
        Snackbar.make(webView, activity.resources.getString(R.string.errorloadpage), Snackbar.LENGTH_LONG)
                .setAction(activity.resources.getString(R.string.btn_ok), object : View.OnClickListener {
                    override fun onClick(v: View) {
                        webView.loadUrl(homepagelink)
                    }
                }).show()
    }

    override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?){
        // 키를 사용하지 못 할 경우
    }

    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, request, error)

        when (error!!.errorCode) {
            WebViewClient.ERROR_AUTHENTICATION -> {
                // 서버에서 사용자 인증 실패
            }
            WebViewClient.ERROR_BAD_URL -> {
                // 잘못된 URL
            }
            WebViewClient.ERROR_CONNECT -> {
                // 서버로 연결 실패
            }
            WebViewClient.ERROR_FAILED_SSL_HANDSHAKE -> {
                // SSL handshake 수행 실패
            }
            WebViewClient.ERROR_FILE -> {
                // 일반 파일 오류
            }
            WebViewClient.ERROR_FILE_NOT_FOUND -> {
                // 파일을 찾을 수 없습니다
            }
            WebViewClient.ERROR_HOST_LOOKUP -> {
                // 서버 또는 프록시 호스트 이름 조회 실패
            }
            WebViewClient.ERROR_IO -> {
                // 서버에서 읽거나 서버로 쓰기 실패
            }
            WebViewClient.ERROR_PROXY_AUTHENTICATION -> {
                // 프록시에서 사용자 인증 실패
            }
            WebViewClient.ERROR_REDIRECT_LOOP -> {
                // 너무 많은 리디렉션
            }
            WebViewClient.ERROR_TIMEOUT -> {
                // 연결 시간 초과
            }
            WebViewClient.ERROR_TOO_MANY_REQUESTS -> {
                // 페이지 로드중 너무 많은 요청 발생
            }
            WebViewClient.ERROR_UNKNOWN -> {
                // 일반 오류
            }
            WebViewClient.ERROR_UNSUPPORTED_AUTH_SCHEME -> {
                // 지원되지 않는 인증 체계
            }
            WebViewClient.ERROR_UNSUPPORTED_SCHEME -> {
                // URI가 지원되지 않는 방식
            }
        }
    }

    override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
        super.onReceivedHttpError(view, request, errorResponse)
    }

}

class JsInterface {
    @JavascriptInterface
    fun processHTML(content: String) {
        //handle content
    }
}
