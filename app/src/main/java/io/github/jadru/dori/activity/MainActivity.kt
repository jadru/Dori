package io.github.jadru.dori.activity

import java.io.File
import kotlinx.android.synthetic.main.activity_main.*

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieSyncManager
import android.webkit.JavascriptInterface
import android.widget.Toast
import io.github.jadru.dori.R
import io.github.jadru.dori.function.reSizeAll
import io.github.jadru.dori.function.reSizewithEdit
import io.github.jadru.dori.web.*
import io.github.jadru.dori.function.saveUrl
import io.github.jadru.dori.function.skinEngine
import io.github.jadru.dori.web.checkStoragePermission
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    // MainActivity Var & Val
    private var decorView: View? = null

    private var uiOption: Int = 0
    internal var yesorno = false
    internal var searchengineurl: String? = ""
    var homepagelink: String = "http://www.naver.com"

    private val MY_PERMISSION_REQUEST_STORAGE = 100
    private val MY_PERMISSION_REQUEST_LOCATION = 101

    var pref: SharedPreferences? = null

    var imm: InputMethodManager? = null

    private val completeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val res = context.resources
            Snackbar.make(bg, resources.getString(R.string.downloaded), Snackbar.LENGTH_LONG)
                    .setAction(resources.getString(R.string.btn_ok)) { startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)) }.show()

        }
    }

    @JavascriptInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkStoragePermission(this)

        pref = getSharedPreferences(packageName + "pref", 0)

//        val params = ConstraintLayout.LayoutParams(
//                ConstraintLayout.LayoutParams.MATCH_PARENT,
//                ConstraintLayout.LayoutParams.MATCH_PARENT
//        )
//        params.setMargins(0, statusBarHeight, 0, navigationBarHeight)
//        webView.layoutParams = params

        setWebView(webView, progressBar)

        window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        window.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)

        val url_before = pref!!.getString("url_before", "http://www.google.com")

        val url = intent.data
        if (url != null) {
            webView.loadUrl(url!!.toString())
            Toast.makeText(this, resources.getString(R.string.connectlink), Toast.LENGTH_SHORT).show()
        } else {
            webView.loadUrl(homepagelink)
        }

        skinEngine(this)
        setsettingsnow()
        window.addFlags(16777216)

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
        webView.webViewClient = WebBrowserClient(this, webView, progressBar, url_edit, homepagelink)

        url_edit.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            // TODO Auto-generated method stub
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
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

    fun setsettingsnow() {
        homepagelink = pref!!.getString("homepage", "http://www.google.com")
        searchengineurl = pref!!.getString("searchengine", "https://www.google.co.kr/search?q=")
        if (!homepagelink.contains("http")) {
            homepagelink = "http://$homepagelink"
        }
    }

    fun downloadImage(uRl: String) {
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
        skinEngine(this)
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
        saveUrl(this, webView)
        skinEngine(this)
    }

    override fun onStop() {
        super.onStop()
        saveUrl(this, webView)
    }

    override fun onRestart() {
        super.onRestart()
        if (intent.data != null) {
            webView.loadUrl(intent.data!!.toString())
        }
        skinEngine(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_FILE_CHOOSE && mUploadMsg != null) {
            val result = if (data == null || resultCode != Activity.RESULT_OK) null else data!!.data
            mUploadMsg!!.onReceiveValue(result)
            mUploadMsg = null
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val player = MediaPlayer()
        if (player.isPlaying) {
            player.stop()
        } else if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
//            if (url_edit.visibility == View.VISIBLE) {
//                url_edit.visibility = View.INVISIBLE
//                val ed = webView.url.toString()
//                url_edit.setText(ed)
//                return false
//            } else {
                webView.goBack()
                return false
//            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        // TODO Auto-generated method stub
        // super.onWindowFocusChanged(hasFocus);

        if (pref!!.getBoolean("fullscreen", false)) {
            if (hasFocus) {
                decorView!!.systemUiVisibility = uiOption
            }
        }
    }
}
