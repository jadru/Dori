package io.github.jadru.dori.web

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.webkit.GeolocationPermissions
import io.github.jadru.dori.activity.MainActivity

var myOrigin: String? = ""
var myCallback: GeolocationPermissions.Callback? = null
private val MY_PERMISSION_REQUEST_LOCATION = 101

fun checkLocationPermission(context: Context) {
    if (((ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Activity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
        }
        ActivityCompat.requestPermissions(Activity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSION_REQUEST_LOCATION)
    } else {
        myCallback!!.invoke(myOrigin, true, false)
    }
}