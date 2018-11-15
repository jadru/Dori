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
import android.widget.Toast
import io.github.jadru.dori.R

private val MY_PERMISSION_REQUEST_STORAGE = 100

fun checkStoragePermission(context: Context) {
    if (((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                    (checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Activity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Explain to the user why we need to write the permission.
            Toast.makeText(context, Activity().resources.getString(R.string.permissionfordownload), Toast.LENGTH_SHORT).show()
        }
        ActivityCompat.requestPermissions(Activity(),arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMISSION_REQUEST_STORAGE)
    }
}