package com.desk360.livechat.presentation.viewholder

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.desk360.base.service.DownloadManagerService
import com.desk360.livechat.data.model.chat.Message
import com.desk360.livechat.manager.Desk360LiveChat
import com.desk360.livechat.manager.FileManager
import com.desk360.livechat.manager.LiveChatHelper

abstract class BaseMediaMessageViewHolder(itemView: View) :
    BaseMessageViewHolder(itemView) {

    abstract fun getDownloadDescription(): String?

    protected fun open(message: Message) {
        val attachment = message.attachments?.firstOrNull()
        val fileName = if (message.isMine) {
            val nameAndExtension = attachment?.name.toString().split('.')
            "${nameAndExtension[0]}_${message.time}.${nameAndExtension[1]}"
        } else attachment?.name.toString()

        var file = FileManager.getFile(fileName)

        if (!file.exists()) {
            download(message)
            file = FileManager.getFile(fileName)
        }

        if (file.exists()) {
            val uri: Uri? = Desk360LiveChat.getContext()?.let { it1 ->
                Desk360LiveChat.getContext()?.packageName?.let { packageName ->
                    FileProvider.getUriForFile(
                        it1,
                        "$packageName.provider",
                        file
                    )
                }
            }

            val url = file
            val intent = Intent(Intent.ACTION_VIEW)
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword")
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf")
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel")
            } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav")
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf")
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav")
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif")
            } else if (url.toString().contains(".jpg") || url.toString()
                    .contains(".jpeg") || url.toString().contains(".png")
            ) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg")
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain")
            } else if (url.toString().contains(".3gp") || url.toString()
                    .contains(".mpg") || url.toString().contains(".mpeg") || url.toString()
                    .contains(".mpe") || url.toString().contains(".mp4") || url.toString()
                    .contains(".avi")
            ) {
                // Video files
                intent.setDataAndType(uri, "video/*")
            } else {
                intent.setDataAndType(uri, "*/*")
            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                itemView.context.startActivity(intent)
            } catch (ex: Exception) {
                Desk360LiveChat.getContext()?.let { context ->
                    Toast.makeText(
                        context,
                        LiveChatHelper.settings?.data?.language?.sdkFileCannotBeOpened,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    protected fun download(message: Message) {
        message.attachments?.firstOrNull()?.let { attachment ->
            attachment.url?.let { url ->
                attachment.name?.let { fileName ->
                    DownloadManagerService.start(
                        url,
                        fileName,
                        getDownloadDescription() ?: "downloading..."
                    )
                }
            }
        }
    }
}