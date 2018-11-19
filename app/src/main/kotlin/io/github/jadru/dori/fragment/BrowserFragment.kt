package io.github.jadru.dori.fragment

import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.webkit.CookieManager

import io.github.jadru.dori.R
import io.github.jadru.dori.activity.BrowserActivity
import io.github.jadru.dori.function.openFromintent
import io.github.jadru.dori.function.saveUrl
import io.github.jadru.dori.function.skinEngine
import io.github.jadru.dori.web.*
import kotlinx.android.synthetic.main.fragment_browser.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BrowserFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BrowserFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class BrowserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    val PREFS_FILENAME = "io.github.jadru.dori.prefs"
    var pref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    val completeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val snack = Snackbar.make(webView, R.string.downloaded, Snackbar.LENGTH_LONG)
            snack.show()

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pref = context!!.getSharedPreferences(PREFS_FILENAME, AppCompatActivity.MODE_PRIVATE)
        setWebView(webView)
        webView.webViewClient = WebBrowserClient(BrowserActivity(), webView, progressBar, url_edit, pref, img_favicon, url_bar)
        webView.webChromeClient = ChromeClient(BrowserActivity(), progressBar)
        openFromintent(webView, activity, pref!!)
        SetUrlEdit(webView, url_edit, context, activity)

        webView.setOnScrollChangeListener { _, _, y, _, oldy -> run{
            if(y + 50 < oldy){
                //show
            }
            if(y > oldy + 50){
                //hide
            }
        } }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                BrowserFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onStart() {
        super.onStart()
        skinEngine(context!!)
        CookieManager.getInstance().acceptCookie()
        if (activity!!.intent.data != null) {
            webView.loadUrl(activity!!.intent.data!!.toString())
        }
    }

    override fun onPause() {
        super.onPause()
        webView.pauseTimers()
        context!!.unregisterReceiver(completeReceiver)
    }

    override fun onResume() {
        super.onResume()
        webView.resumeTimers()
        val completeFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        context!!.registerReceiver(completeReceiver, completeFilter)
        if (activity!!.intent.data != null) {
            webView.loadUrl(activity!!.intent.data!!.toString())
        }
        saveUrl(webView, pref!!)
        skinEngine(context!!)
    }

    override fun onStop() {
        super.onStop()
        saveUrl(webView, pref!!)
    }

    fun myOnKeyDown(key_code: Int):Boolean {
        if (key_code == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return false
        }else{
            return true
        }
    }

}
