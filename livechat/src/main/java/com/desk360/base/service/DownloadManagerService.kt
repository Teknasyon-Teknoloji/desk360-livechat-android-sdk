package com.desk360.base.service

import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Environment
import com.desk360.livechat.manager.Desk360LiveChat

object DownloadManagerService {
    private var INSTANCE: DownloadManager? = null
    private val instance: DownloadManager
        get() {
            if (INSTANCE == null) {
                INSTANCE = Desk360LiveChat.getContext()?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            }
            return INSTANCE!!
        }

    fun start(url: String, fileName: String, description: String): Long {
        val downloadRequest = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription(description)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(
                Desk360LiveChat.getContext(),
                Environment.DIRECTORY_DOWNLOADS,
                fileName
            )
            .setAllowedOverMetered(true) // Mobile Network todo: boyutu büyük olan dosyalar için wifi bağlantısını zorunlu yapmamız gerekebilir.
            .setAllowedOverRoaming(true) // Roaming Network
        return instance.enqueue(downloadRequest)
    }
}