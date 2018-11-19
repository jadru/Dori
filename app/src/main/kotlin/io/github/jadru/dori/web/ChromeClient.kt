package io.github.jadru.dori.web

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import android.widget.FrameLayout
import android.widget.ProgressBar
import io.github.jadru.dori.R
import io.github.jadru.dori.activity.BrowserActivity

class ChromeClient(activity: BrowserActivity, var proBar:ProgressBar) : WebChromeClient() {

    protected var mActivity: Activity? = null
    private var mCustomView: View? = null
    private var mCustomViewCallback: WebChromeClient.CustomViewCallback? = null
    private var mOriginalOrientation: Int = 0

    private val mContentView: FrameLayout? = null
    private var mFullscreenContainer: FrameLayout? = null

    init {
        this.mActivity = activity
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        proBar.progress = newProgress
    }

    // 위치 정보 수집을 위한 정보 수집
    override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
        myOrigin = origin
        myCallback = callback
        checkLocationPermission(mActivity!!)
    }

//    fun openFileChooser(uploadFile: ValueCallback<Uri>, acceptType: String) {
//        openFileChooser(uploadFile)
//    }
//
//    fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
//        mUploadMsg = uploadMsg
//        val i = Intent(Intent.ACTION_GET_CONTENT)
//        i.addCategory(Intent.CATEGORY_OPENABLE)
//        i.type = "*/*"
//        Activity().startActivityForResult(Intent.createChooser(
//                i, mActivity!!.resources.getString(R.string.filechooser)), RC_FILE_CHOOSE)
//    }
//
//    override fun onCreateWindow(view: WebView, dialog: Boolean,
//                                userGesture: Boolean, resultMsg: Message): Boolean {
//        // TODO Auto-generated method stub
//        return super.onCreateWindow(view, dialog, userGesture, resultMsg)
//    }

    override fun onJsConfirm(view: WebView, url: String, message: String,
                             result: JsResult): Boolean {
        // TODO Auto-generated method stub
        //return super.onJsConfirm(view, url, message, result);
        AlertDialog.Builder(view.context)
                .setTitle(mActivity!!.resources.getString(R.string.alert))
                .setMessage(message)
                .setPositiveButton(mActivity!!.resources.getString(R.string.btn_ok)){ _, _ ->
                    result.confirm()
                }

                .setNegativeButton(mActivity!!.resources.getString(R.string.btn_no)){ _, _ ->
                    result.cancel()
                }
                .setCancelable(false)
                .create()
                .show()
        return true
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        super.onShowCustomView(view, callback)
        if (mCustomView != null) {
            callback!!.onCustomViewHidden()
            return
        }

        mOriginalOrientation = mActivity!!.getRequestedOrientation()
        val decor = mActivity!!.window.decorView as FrameLayout
        mFullscreenContainer = FullscreenHolder(mActivity!!)
        mFullscreenContainer!!.addView(view, COVER_SCREEN_PARAMS)
        decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS)
        mCustomView = view
        setFullscreen(true)
        mCustomViewCallback = callback
//        mActivity!!.setRequestedOrientation(requestedOrientation)

        super.onShowCustomView(view, callback)
    }

    override
    fun onHideCustomView() {
        if (mCustomView == null) {
            return
        }

        setFullscreen(false)
        val decor = mActivity!!.window.decorView as FrameLayout
        decor.removeView(mFullscreenContainer)
        mFullscreenContainer = null
        mCustomView = null
        mCustomViewCallback!!.onCustomViewHidden()
        mActivity!!.requestedOrientation = mOriginalOrientation
    }

    private fun setFullscreen(enabled: Boolean) {
        val win = mActivity!!.window
        val winParams = win.attributes
        val bits = WindowManager.LayoutParams.FLAG_FULLSCREEN
        if (enabled) {
            winParams.flags or bits
        } else {
            winParams.flags and bits
            if (mCustomView != null) {
                mCustomView!!.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            } else {
                mContentView!!.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
        win.attributes = winParams
    }

    private class FullscreenHolder(ctx: Context) : FrameLayout(ctx) {
        init {
            setBackgroundColor(ctx.resources.getColor(android.R.color.black))
        }

        override
        fun onTouchEvent(evt: MotionEvent): Boolean {
            return true
        }
    }

    companion object {
        private val COVER_SCREEN_PARAMS = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onShowFileChooser(webView:WebView, filePathCallback:ValueCallback<Array<Uri>>, fileChooserParams:FileChooserParams):Boolean {
        var mFilePathCallback = filePathCallback
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = ("*/*")
        val PICKFILE_REQUEST_CODE = 100
        mActivity!!.startActivityForResult(intent, PICKFILE_REQUEST_CODE)
        return true
    }

    override fun onReceivedTouchIconUrl(view: WebView, url: String, precomposed: Boolean) {
        super.onReceivedTouchIconUrl(view, url, precomposed)
    } // 사이트 터치 아이콘

}