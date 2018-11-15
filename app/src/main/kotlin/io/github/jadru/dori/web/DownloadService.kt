package io.github.jadru.dori.web

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import android.webkit.WebView

fun DownloadService(webView: WebView){

    webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
        val mtm = MimeTypeMap.getSingleton()
        val downloadManager = webView.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
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