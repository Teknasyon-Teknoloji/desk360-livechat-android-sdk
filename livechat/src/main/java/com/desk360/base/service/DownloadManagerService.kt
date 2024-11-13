package com.desk360.base.service

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Environment
import com.desk360.livechat.manager.Desk360LiveChat

object DownloadManagerService {
    private val context: Context?
        get() = Desk360LiveChat.getContext()

    private val downloadManager: DownloadManager? by lazy {
        Desk360LiveChat.getContext()?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
    }

    fun start(url: String, fileName: String, description: String): Long? {
        val context = this.context ?: return null
        val downloadManager = this.downloadManager ?: return null

        val downloadRequest = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription(description)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_DOWNLOADS,
                fileName
            )
            .setAllowedOverMetered(true) // Mobile Network todo: boyutu büyük olan dosyalar için wifi bağlantısını zorunlu yapmamız gerekebilir.
            .setAllowedOverRoaming(true) // Roaming Network

        return downloadManager.enqueue(downloadRequest)
    }
}