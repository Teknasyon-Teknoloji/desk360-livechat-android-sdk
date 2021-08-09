package com.desk360.base.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.net.Uri
import com.desk360.base.util.Utils
import com.desk360.livechat.manager.Desk360LiveChat
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import io.reactivex.plugins.RxJavaPlugins

class ChatProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        EmojiManager.install(GoogleEmojiProvider())
        RxJavaPlugins.setErrorHandler {
            Desk360LiveChat.getContext()?.let { it1 -> Utils.handleError(it1, it) }
        }

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
}