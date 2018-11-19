@file:JvmName("the_browser")
package io.github.jadru.dori.activity

import java.io.File

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.JavascriptInterface
import android.widget.Toast
import io.github.jadru.dori.R
import io.github.jadru.dori.fragment.BrowserFragment
import io.github.jadru.dori.web.*
import io.github.jadru.dori.function.skinEngine
import io.github.jadru.dori.web.checkStoragePermission

class BrowserActivity : AppCompatActivity(), BrowserFragment.OnFragmentInteractionListener{

    private val MY_PERMISSION_REQUEST_STORAGE = 100
    private val MY_PERMISSION_REQUEST_LOCATION = 101
    private var decorView: View? = null
    private var uiOption: Int = 0
    var homepagelink: String? = "http://www.naver.com"
    val PREFS_FILENAME = "io.github.jadru.dori.prefs"
    var pref: SharedPreferences? = null

    @JavascriptInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)

        pref = this.getSharedPreferences(PREFS_FILENAME, MODE_PRIVATE)
        addFragment(BrowserFragment(), R.id.fragment)
        checkStoragePermission(this)

        window.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)

        skinEngine(this)
        setsettingsnow()
        window.addFlags(16777216)

    }

    fun setsettingsnow() {
        homepagelink = pref!!.getString("homepage", "http://www.google.com")
        if (!homepagelink!!.contains("http")) {
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
        mgr.enqueue(request)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_FILE_CHOOSE && mUploadMsg != null) {
            val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
            mUploadMsg!!.onReceiveValue(result)
            mUploadMsg = null
        }
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

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    private fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int){
        supportFragmentManager.inTransaction { add(frameId, fragment) }
    }


    fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction{replace(frameId, fragment)}
    }

    override fun onRestart() {
        super.onRestart()
        skinEngine(this)
    }

    override fun onFragmentInteraction(uri: Uri){

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val player = MediaPlayer()
        if (player.isPlaying) {
            player.stop()
        }
        if(BrowserFragment().myOnKeyDown(keyCode))
            return super.onKeyDown(keyCode, event)
        else
            return false

    }

}
