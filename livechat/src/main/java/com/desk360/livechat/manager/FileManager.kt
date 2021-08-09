package com.desk360.livechat.manager

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import okhttp3.ResponseBody
import java.io.*
import java.nio.file.Files

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.KITKAT)
object FileManager {

    const val IMAGE_TYPE = "image/*"
    const val VIDEO_TYPE = "video/*"
    const val APPLICATION_TYPE = "application/*"

    val path = Desk360LiveChat.getContext()?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path

    fun getFile(fileName: String): File {
        return File(path + File.separator + fileName)
    }

    fun isExists(fileName: String): Boolean {
        return getFile(fileName).exists()
    }

    fun createIntentForImages() =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            .apply {
                type = IMAGE_TYPE
            }

    fun createIntentForVideos() =
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            .apply {
                type = VIDEO_TYPE
            }

    /* Get uri related content real local file path. */
    fun getUriRealPath(ctx: Context, uri: Uri): String {
        var ret = ""
        ret = if (isAboveKitKat) { // Android OS above sdk version 19.
            getUriRealPathAboveKitkat(ctx, uri)
        } else { // Android OS below sdk version 19
            getImageRealPath(ctx.contentResolver, uri, null)
        }
        return ret
    }

    private fun getUriRealPathAboveKitkat(
        ctx: Context?,
        uri: Uri?
    ): String {
        var ret = ""
        if (ctx != null && uri != null) {
            if (isContentUri(uri)) {
                ret = if (isGooglePhotoDoc(uri.authority!!)) {
                    uri.lastPathSegment!!
                } else {
                    getImageRealPath(ctx.contentResolver, uri, null)
                }
            } else if (isFileUri(uri)) {
                ret = uri.path!!
            } else if (isDocumentUri(ctx, uri)) { // Get uri related document id.
                val documentId = DocumentsContract.getDocumentId(uri)
                // Get uri authority.
                val uriAuthority = uri.authority
                if (isMediaDoc(uriAuthority!!)) {
                    val idArr = documentId.split(":").toTypedArray()
                    if (idArr.size == 2) { // First item is document type.
                        val docType = idArr[0]
                        // Second item is document real id.
                        val realDocId = idArr[1]
                        // Get content uri by document type.
                        var mediaContentUri =
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        when (docType) {
                            "image" -> {
                                mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            }
                            "video" -> {
                                mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            }
                            "audio" -> {
                                mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                            }
                        }
                        // Get where clause with real document id.
                        val whereClause =
                            MediaStore.Images.Media._ID + " = " + realDocId
                        ret =
                            getImageRealPath(ctx.contentResolver, mediaContentUri, whereClause)
                    }
                } else if (isDownloadDoc(uriAuthority)) { // Build download uri.
                    val downloadUri =
                        Uri.parse("content://downloads/public_downloads")
                    // Append download document id at uri end.
                    val downloadUriAppendId =
                        ContentUris.withAppendedId(downloadUri, java.lang.Long.valueOf(documentId))
                    ret = getImageRealPath(ctx.contentResolver, downloadUriAppendId, null)
                } else if (isExternalStoreDoc(uriAuthority)) {
                    val idArr = documentId.split(":").toTypedArray()
                    if (idArr.size == 2) {
                        val type = idArr[0]
                        val realDocId = idArr[1]
                        if ("primary".equals(type, ignoreCase = true)) {
                            ret =
                                Environment.getExternalStorageDirectory()
                                    .toString() + "/" + realDocId
                        }
                    }
                }
            }
        }
        return ret
    }

    /* Check whether current android os version is bigger than kitkat or not. */
    private val isAboveKitKat: Boolean
        get() {
            var ret = false
            ret = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
            return ret
        }

    /* Check whether this uri represent a document or not. */
    private fun isDocumentUri(ctx: Context?, uri: Uri?): Boolean {
        var ret = false
        if (ctx != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(ctx, uri)
        }
        return ret
    }

    /* Check whether this uri is a content uri or not.
     *  content uri like content://media/external/images/media/1302716
     *  */
    private fun isContentUri(uri: Uri?): Boolean {
        var ret = false
        if (uri != null) {
            val uriSchema = uri.scheme
            if ("content".equals(uriSchema, ignoreCase = true)) {
                ret = true
            }
        }
        return ret
    }

    /* Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     * */
    private fun isFileUri(uri: Uri?): Boolean {
        var ret = false
        if (uri != null) {
            val uriSchema = uri.scheme
            if ("file".equals(uriSchema, ignoreCase = true)) {
                ret = true
            }
        }
        return ret
    }

    /* Check whether this document is provided by ExternalStorageProvider. */
    fun isExternalStoreDoc(uriAuthority: String): Boolean {
        var ret = false
        if ("com.android.externalstorage.documents" == uriAuthority) {
            ret = true
        }
        return ret
    }

    /* Check whether this document is provided by DownloadsProvider. */
    private fun isDownloadDoc(uriAuthority: String): Boolean {
        var ret = false
        if ("com.android.providers.downloads.documents" == uriAuthority) {
            ret = true
        }
        return ret
    }

    /* Check whether this document is provided by MediaProvider. */
    private fun isMediaDoc(uriAuthority: String): Boolean {
        var ret = false
        if ("com.android.providers.media.documents" == uriAuthority) {
            ret = true
        }
        return ret
    }

    /* Check whether this document is provided by google photos. */
    private fun isGooglePhotoDoc(uriAuthority: String): Boolean {
        var ret = false
        if ("com.google.android.apps.photos.content" == uriAuthority) {
            ret = true
        }
        return ret
    }

    /* Return uri represented document file real local path.*/
    private fun getImageRealPath(
        contentResolver: ContentResolver,
        uri: Uri,
        whereClause: String?
    ): String {
        var ret = ""
        // Query the uri with condition.
        val cursor =
            contentResolver.query(uri, null, whereClause, null, null)
        if (cursor != null) {
            val moveToFirst = cursor.moveToFirst()
            if (moveToFirst) { // Get columns name by uri type.
                var columnName = MediaStore.Images.Media.DATA
                when {
                    uri === MediaStore.Images.Media.EXTERNAL_CONTENT_URI -> {
                        columnName = MediaStore.Images.Media.DATA
                    }
                    uri === MediaStore.Audio.Media.EXTERNAL_CONTENT_URI -> {
                        columnName = MediaStore.Audio.Media.DATA
                    }
                    uri === MediaStore.Video.Media.EXTERNAL_CONTENT_URI -> {
                        columnName = MediaStore.Video.Media.DATA
                    }
                }
                // Get column index.
                val imageColumnIndex = cursor.getColumnIndex(columnName)
                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex)
            }
        }

        cursor?.close()
        return ret
    }

    fun saveFile(fileName: String, body: ResponseBody?): Boolean {
        try {

            val futureStudioIconFile = getFile(fileName)
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize =
                    body!!.contentLength() // todo: eğer 20MB büyükse göndermeyeceğiz.....
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                }
                outputStream.flush()
            } catch (e: IOException) {

            } finally {
                inputStream?.close()
                outputStream?.close()
                return true
            }
        } catch (e: IOException) {
            return false
        }
    }

    @Throws(IOException::class)
    fun copyFile(sourceFile: File?, fileName: String) {
        var inputStream: FileInputStream? = null
        try {
            val destinationFile = getFile(fileName)
            inputStream = FileInputStream(sourceFile)
            Files.copy(inputStream, destinationFile.toPath())
        } finally {
            inputStream?.close()
        }
    }

    fun getBitmapDrawable(fileName: String): BitmapDrawable {
        val thumb = ThumbnailUtils.createVideoThumbnail(
            getFile(fileName).path,
            MediaStore.Images.Thumbnails.MINI_KIND
        )
        return BitmapDrawable(thumb)
    }

    object Extensions {
        const val XLSX = "xlsx"
        const val XLS = "xls"
        const val DOCX = "docx"
        const val DOC = "doc"
        const val PDF = "pdf"
    }

    object MediaType {
        const val IMAGES = "images"
        const val VIDEOS = "videos"
        const val FILES = "files"
    }

    object ChatType {
        const val LIVE = "livechat"
        const val ONE_TWO_ONE = "one_two_one"
        const val GROUP = "group"
    }
}