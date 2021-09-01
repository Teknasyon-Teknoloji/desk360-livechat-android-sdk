package com.desk360.base.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.net.Uri
import android.os.Looper
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins


class ChatProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        EmojiManager.install(GoogleEmojiProvider())
        setUpRxPlugin()

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ) = null

    override fun getType(uri: Uri) = null

    override fun insert(uri: Uri, values: ContentValues?) = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ) = 0

    companion object {
        private fun setUpRxPlugin() {
            val asyncMainThreadScheduler = AndroidSchedulers.from(Looper.getMainLooper(), true)
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { asyncMainThreadScheduler }
            RxJavaPlugins.setErrorHandler { th: Throwable ->

            }
        }
    }
}