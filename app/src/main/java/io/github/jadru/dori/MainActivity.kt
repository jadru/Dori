package io.github.jadru.dori

import java.io.File
import java.net.URISyntaxException
import kotlinx.android.synthetic.main.activity_main.*

import android.Manifest
import android.content.ClipboardManager
import android.content.SharedPreferences

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Picture
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.CookieSyncManager
import android.webkit.GeolocationPermissions
import android.webkit.JavascriptInterface
import android.webkit.JsResult
import android.webkit.MimeTypeMap
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebChromeClient.CustomViewCallback
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var decorView: View? = null

    private var uiOption: Int = 0
    internal var yesorno = false
    internal var searchengineurl: String? = ""
    internal var homepagelink: String? = ""

    internal var black: Boolean = false
    internal var mBitmap: Bitmap? = null
    internal var bgcolor: Int = 0
    internal var statusbarcolor: Int = 0
    private var mCustomView: View? = null

    private var mOriginalOrientation: Int = 0
    private var mFullscreenContainer: FullscreenHolder? = null
    private var mCustomViewCollback: CustomViewCallback? = null

    private val MY_PERMISSION_REQUEST_STORAGE = 100
    private val MY_PERMISSION_REQUEST_LOCATION = 101
    private var deviceName: String? = null


    var prefsdef: SharedPreferences? = null
    var pref: SharedPreferences? = null

    var imm = getSystemService(Context.INPUT_METHOD_SERVICE)

    private val RC_FILE_CHOOSE = 1
    private var mUploadMsg: ValueCallback<Uri>? = null

    val TAG: String? = null

    var myOrigin: String? = ""
    var myCallback: GeolocationPermissions.Callback? = null

    private val completeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val res = context.resources
            Snackbar.make(bg, resources.getString(R.string.downloaded), Snackbar.LENGTH_LONG)
                    .setAction(resources.getString(R.string.btn_ok)) { startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)) }.show()

        }
    }

    val statusBarHeight: Int
        get() {
            var result = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

    @JavascriptInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        deviceName = android.os.Build.MODEL

        checkStoragePermission()

        pref = this.getSharedPreferences(packageName + "pref", 0)
        prefsdef = PreferenceManager.getDefaultSharedPreferences(this)

        val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        )
        params.setMargins(0, statusBarHeight, 0, 0)
        webView.layoutParams = params
        setWebView()

        window.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)

        val url_before = pref.getString("url_before", "http://www.google.com")

        val url = intent.data
        if (url != null) {
            webView.loadUrl(url!!.toString())
            Toast.makeText(this, resources.getString(R.string.connectlink), Toast.LENGTH_SHORT).show()
        } else {
            webView.loadUrl(homepagelink)
        }

        themeSkin()
        window.addFlags(16777216)

    }

    internal class FullscreenHolder(ctx: Context) : FrameLayout(ctx) {

        init {
            setBackgroundColor(ctx.resources.getColor(android.R.color.black))
        }

        override fun onTouchEvent(evt: MotionEvent): Boolean {
            return true
        }
    }

    fun setWebView() {
        val webSettings = webView.settings  // 웹세팅 객체 생성
        webSettings.saveFormData = true  // 캐시 사용 허용a
        webSettings.setSupportZoom(true)  // 줌 지원
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
        webView.isScrollbarFadingEnabled = true
        webView.scrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY
        webView.isVerticalScrollBarEnabled = true
        webView.isHorizontalScrollBarEnabled = false
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 5.1.1; " + deviceName + "Build/LMY48B; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/43.0.2357.65 Mobile Safari/537.36")
        webView.webViewClient = WebBrowserClient()  // 웹뷰에 브라우저 클라이언트 기능 활성화
        webView.requestFocus()

        WebView.setWebContentsDebuggingEnabled(true)


        registerForContextMenu(webView)
        val context = webView.context
        val packageManager = context.packageManager
        val appName = ""
        val appVersion = ""
        val userAgent = webSettings.userAgentString

        webView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progressBar.progress = newProgress
            }

            override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
                myOrigin = origin
                myCallback = callback
                checkLocationPermission()
            }

            fun openFileChooser(uploadFile: ValueCallback<Uri>, acceptType: String) {
                openFileChooser(uploadFile)
            }

            fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
                mUploadMsg = uploadMsg
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "*/*"
                startActivityForResult(Intent.createChooser(i, resources.getString(R.string.filechooser)), RC_FILE_CHOOSE)
            }

            override fun onCreateWindow(view: WebView, dialog: Boolean,
                                        userGesture: Boolean, resultMsg: Message): Boolean {
                // TODO Auto-generated method stub
                return super.onCreateWindow(view, dialog, userGesture, resultMsg)
            }

            override fun onJsConfirm(view: WebView, url: String, message: String,
                                     result: JsResult): Boolean {
                // TODO Auto-generated method stub
                //return super.onJsConfirm(view, url, message, result);
                AlertDialog.Builder(view.context)
                        .setTitle(resources.getString(R.string.alert))
                        .setMessage(message)
                        .setPositiveButton(resources.getString(R.string.btn_ok)){dialog, which ->
                                        result.confirm()
                                    }

                        .setNegativeButton(resources.getString(R.string.btn_no)){dialog, which ->
                                        result.cancel()
                                    }
                        .setCancelable(false)
                        .create()
                        .show()
                return true
            }

            override fun onShowCustomView(view: View, callback: CustomViewCallback) {

                if (mCustomView != null) {
                    callback.onCustomViewHidden()
                    return
                }

                mOriginalOrientation = this@MainActivity.requestedOrientation

                val decor = this@MainActivity.window.decorView as FrameLayout

                mFullscreenContainer = FullscreenHolder(this@MainActivity)
                mFullscreenContainer!!.addView(view, ViewGroup.LayoutParams.MATCH_PARENT)
                decor.addView(mFullscreenContainer, ViewGroup.LayoutParams.MATCH_PARENT)
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN)
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                mCustomView = view
                mCustomViewCollback = callback
                this@MainActivity.requestedOrientation = mOriginalOrientation

            }

            override fun onHideCustomView() {
                if (mCustomView == null) {
                    return
                }

                val decor = this@MainActivity.window.decorView as FrameLayout
                decor.removeView(mFullscreenContainer)
                mFullscreenContainer = null
                mCustomView = null
                mCustomViewCollback!!.onCustomViewHidden()
                this@MainActivity.requestedOrientation = mOriginalOrientation
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            }

        }

        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            val mtm = MimeTypeMap.getSingleton()
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri = Uri.parse(url)

            var fileName = downloadUri.lastPathSegment
            var pos = 0
            pos = contentDisposition.toLowerCase().lastIndexOf("filename=")
            if (pos >= 0) {
                fileName = contentDisposition.substring(pos + 9)
                pos = fileName.lastIndexOf(";")
                if (pos > 0) {
                    fileName = fileName.substring(0, pos - 1)
                }
            }

            val fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length).toLowerCase()
            val mimeType = mtm.getMimeTypeFromExtension(fileExtension)
            val request = DownloadManager.Request(downloadUri)
            request.setTitle(fileName)
            request.setDescription(url)
            request.setMimeType(mimeType)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadManager!!.enqueue(request)
        }

    }

    internal inner class WebBrowserClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            var url = url
            if (url.startsWith("http")) {
                webView.loadUrl(url)
            } else if (url.startsWith("copy")) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard!!.text = url
            } else if (url.startsWith("sms")) {
                if (url.split(("=").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size > 1) {
                    url = url.split(("=").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                }
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("sms:")
                intent.putExtra("sms_body", url)
                startActivity(intent)
            } else if (url.startsWith("kakolink:")) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } else if (url.startsWith("mailto:")) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } else if (url.startsWith("tel")) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } else if (url.startsWith("intent://")) {
                try {
                    val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                    val existPackage = packageManager.getLaunchIntentForPackage(intent.getPackage()!!)
                    if (existPackage != null) {
                        startActivity(intent)
                    } else {
                        val marketIntent = Intent(Intent.ACTION_VIEW)
                        marketIntent.data = Uri.parse("market://details?id=" + intent.getPackage()!!)
                        startActivity(marketIntent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else if (url.startsWith("market://")) {
                try {
                    val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                    if (intent != null) {
                        startActivity(intent)
                    }
                } catch (e: URISyntaxException) {
                    e.printStackTrace()
                }

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
            return true
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            val sslagree = prefsdef.getBoolean("sslagree", false)
            if (sslagree) {
                val alt_bld = AlertDialog.Builder(this@MainActivity)
                alt_bld.setMessage(resources.getString(R.string.ssltitle)).setCancelable(
                        false)
                        .setPositiveButton(resources.getString(R.string.btn_no)
                        ) { dialog, id ->
                            handler.cancel()
                            webView.goBack()
                        }
                        .setNegativeButton(resources.getString(R.string.btn_ok),
                                object : DialogInterface.OnClickListener {
                                    override fun onClick(dialog: DialogInterface, id: Int) {
                                        handler.proceed()  //SSL 에러가 발생해도 계속 진행!
                                        Snackbar.make(bg, resources.getString(R.string.recommendout), Snackbar.LENGTH_LONG)
                                                .setAction(resources.getString(R.string.back), object : OnClickListener {
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
                val alt_bld = AlertDialog.Builder(this@MainActivity)
                alt_bld.setMessage(resources.getString(R.string.ssltitle)).setCancelable(
                        false)
                        .setPositiveButton(resources.getString(R.string.back),
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

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {  // 페이지로딩이 시작되면
            super.onPageStarted(view, url, favicon)
            progressBar.visibility = View.VISIBLE  // 프로그레스바 보이기
            if (url_edit.isFocused) {
                url_edit.clearFocus()
                url_edit.visibility = View.INVISIBLE
            }
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            progressBar.visibility = View.INVISIBLE
            webView.isScrollContainer = true
            if (!(url_edit.isFocused)) {
                val ed = webView.url
                url_edit.setText(ed)
            }
            CookieSyncManager.getInstance().sync()
        }

        fun whenError() {
            webView.loadUrl("file:///android_asset/error.html")
            Snackbar.make(bg, resources.getString(R.string.errorloadpage), Snackbar.LENGTH_LONG)
                    .setAction(resources.getString(R.string.btn_ok), object : OnClickListener {
                        override fun onClick(v: View) {
                            webView.loadUrl(homepagelink)
                        }
                    }).show()
        }
    }


    private fun checkStoragePermission() {
        if (((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, resources.getString(R.string.permissionfordownload), Toast.LENGTH_SHORT).show()
            }
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSION_REQUEST_STORAGE)
        }
    }

    private fun checkLocationPermission() {
        if (((checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            }
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSION_REQUEST_LOCATION)
        } else {
            myCallback!!.invoke(myOrigin, true, false)
        }
    }

    fun themeSkin() {
        val expandbtncolor: Int
        val btnurlbg: Int
        val btnmenuimg: Int
        val btntabimg: Int
        val d: Drawable?
        val bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().path + "/Themebrowser/themenow")
        if (bitmap == null) {
            d = null
        } else {
            d = BitmapDrawable(resources, bitmap)
        }
        black = prefsdef.getBoolean("t-black", false)
        bgcolor = pref.getInt("t-color", Color.parseColor("#4dd0e1"))
        if (black) {
            bg.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            bg.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        }
    }

    fun downloadFile(uRl: String) {
        val direct = File(((Environment.getExternalStorageDirectory()).toString() + "/Download"))
        if (!direct.exists()) {
            direct.mkdirs()
        }
        val mgr = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(uRl)
        val request = DownloadManager.Request(
                downloadUri)
        request.setAllowedNetworkTypes(
                (DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE))
                .setAllowedOverRoaming(false).setTitle(uRl)
                .setDescription("Image")
                .setDestinationInExternalPublicDir("/Download", uRl + "jpg")
        mgr!!.enqueue(request)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSION_REQUEST_STORAGE -> if (!((grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED))) {
                Toast.makeText(this, resources.getString(R.string.problemdownload), Toast.LENGTH_SHORT).show()
            }
            MY_PERMISSION_REQUEST_LOCATION -> if (!((grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED))) {
            }
        }
    }

    override fun onStart() {
        super.onStart()
        themeSkin()
        CookieSyncManager.createInstance(this)
        if (intent.data != null) {
            webView.loadUrl(intent.data!!.toString())
        }
    }

    override fun onPause() {
        super.onPause()
        CookieSyncManager.getInstance().stopSync()
        webView.pauseTimers()
        unregisterReceiver(completeReceiver)
        val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = pref.edit()
        val url_before = webView.url
        editor.putString("url_before", url_before)
        editor.putInt("status_before", statusbarcolor)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        webView.resumeTimers()
        val completeFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        registerReceiver(completeReceiver, completeFilter)
        CookieSyncManager.getInstance().startSync()
        if (intent.data != null) {
            webView.loadUrl(intent.data!!.toString())
        }
        val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt("status_before", statusbarcolor)
        editor.apply()
        themeSkin()
    }

    override fun onStop() {
        super.onStop()
        val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = pref.edit()
        val url_before = webView.url
        editor.putString("url_before", url_before)
        editor.putInt("status_before", statusbarcolor)
        editor.apply()
    }

    override fun onRestart() {
        super.onRestart()
        if (intent.data != null) {
            webView.loadUrl(intent.data!!.toString())
        }
        themeSkin()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_FILE_CHOOSE && mUploadMsg != null) {
            val result = if (data == null || resultCode != Activity.RESULT_OK) null else data!!.data
            mUploadMsg!!.onReceiveValue(result)
            mUploadMsg = null
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val player = MediaPlayer()
        if (player.isPlaying) {
            player.stop()
        } else if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            if (url_edit.visibility == View.VISIBLE) {
                url_edit.visibility = View.INVISIBLE
                val ed = webView.url.toString()
                url_edit.setText(ed)
                return false
            } else {
                webView.goBack()
                val mHandler: Handler
                val mRunnable: Runnable
                mRunnable = object : Runnable {
                    override fun run() {
                        val ed = webView.url
                        url_edit.setText(ed)
                    }
                }
                mHandler = Handler()
                mHandler.postDelayed(mRunnable, 300)
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        // TODO Auto-generated method stub
        // super.onWindowFocusChanged(hasFocus);

        if (prefsdef.getBoolean("fullscreen", false)) {
            if (hasFocus) {
                decorView!!.systemUiVisibility = uiOption
            }
        }
    }
}
