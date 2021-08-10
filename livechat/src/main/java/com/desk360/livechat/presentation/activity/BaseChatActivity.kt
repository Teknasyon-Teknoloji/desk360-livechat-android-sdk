package com.desk360.livechat.presentation.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.PopupMenu
import androidx.activity.result.ActivityResult
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.desk360.base.manager.SharedPreferencesManager
import com.desk360.base.presentation.popup.ChatPopup
import com.desk360.base.util.Utils
import com.desk360.livechat.R
import com.desk360.livechat.databinding.ActivityChatBinding
import com.desk360.livechat.manager.FileManager
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.presentation.activity.livechat.ChatViewModel
import com.desk360.livechat.presentation.adapter.MessageListAdapter
import com.vanniktech.emoji.EmojiPopup
import java.io.*
import java.util.*

abstract class BaseChatActivity : BaseActivity<ActivityChatBinding, ChatViewModel>() {
    companion object {
        const val PERMISSION_IMAGE_REQUEST_CODE = 111
        const val PERMISSION_VIDEO_REQUEST_CODE = 222
        const val PERMISSION_DOCUMENT_REQUEST_CODE = 333

        const val EXTRA_CONVERSATION_ID = "extra_conversation_id"

        val permissionsStorage = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private var emojiPopup: EmojiPopup? = null
    var popupMenu: PopupMenu? = null
    private var messageListAdapter: MessageListAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null

    override fun getLayoutResId() = R.layout.activity_chat

    override fun getViewModelClass() = ChatViewModel::class.java

    override fun initUI() {
        binding.viewModel = viewModel

        binding.imageViewArrowDown.setOnClickListener {
            binding.recyclerViewMessageList.postDelayed({
                messageListAdapter?.itemCount?.let { count ->
                    binding.recyclerViewMessageList.smoothScrollToPosition(count)
                }
            }, 100)
        }

        binding.imageViewSendMessage.setOnClickListener {
            val name =
                SharedPreferencesManager.instance?.read(SharedPreferencesManager.USER_NAME, "")

            if (name?.isNotEmpty() == true) {
                val message = binding.editTextMessage.text?.toString()?.trim()
                if (!message.isNullOrEmpty() && viewModel.isSending.value == false && viewModel.hasConnection.value == true) {
                    viewModel.isSending.value = true
                    if (LiveChatHelper.settings?.data?.chatbot == true) {
                        viewModel.sendChatbotMessage(message)
                    } else
                        viewModel.sendMessage(name, message)

                    binding.editTextMessage.setText("")
                    viewModel.isSending.value = false
                }
            }
        }

        binding.toolbarHeader.imageViewBack.setOnClickListener {
            LiveChatHelper.closeKeyboard(this, it)
            onBackPressed()
        }

        binding.editTextMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.setTyping(s.toString().trim())
            }
        })

        initMessageList()
        createEmojiPopup()
        initAttachment()
    }

    private fun initMessageList() {
        messageListAdapter = MessageListAdapter()
        linearLayoutManager =
            LinearLayoutManager(this@BaseChatActivity, LinearLayoutManager.VERTICAL, false)

        binding.recyclerViewMessageList.apply {
            adapter = messageListAdapter
            layoutManager = linearLayoutManager
            setHasFixedSize(false)
        }

        binding.recyclerViewMessageList.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom)
                binding.recyclerViewMessageList.postDelayed({
                    messageListAdapter?.itemCount?.let { count ->
                        binding.recyclerViewMessageList.smoothScrollToPosition(count)
                    }
                }, 100)
        }

        binding.recyclerViewMessageList.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val position: Int = (linearLayoutManager?.findLastVisibleItemPosition() ?: 0) + 1
                if (newState == 0) {
                    binding.imageViewArrowDown.visibility =
                        if ((messageListAdapter?.itemCount
                                ?: 0) - position > 2
                        ) View.VISIBLE else View.GONE
                }
            }
        })
    }

    override fun initObservers() {
        viewModel.conversationId = intent.getStringExtra(EXTRA_CONVERSATION_ID)
        viewModel.checkStatus(1)

        viewModel.isOffline.observe(this, { isOffline ->
            Glide.with(this)
                .load(if (isOffline) R.drawable.ic_offline else R.drawable.ic_online)
                .apply(RequestOptions().circleCrop())
                .into(binding.toolbarHeader.imageViewStatus)

            Glide.with(this)
                .load(if (isOffline) R.drawable.ic_offline else R.drawable.ic_online)
                .apply(RequestOptions().circleCrop())
                .into(binding.toolbarHeader.imageViewStatusBg)
        })

        viewModel.newMessages.observe(this, { messages ->
            messageListAdapter?.submitList(messages)

            binding.recyclerViewMessageList.postDelayed({
                messageListAdapter?.itemCount?.let { count ->
                    binding.recyclerViewMessageList.smoothScrollToPosition(count)
                }
            }, 100)
        })

        viewModel.isSentMessageSuccessful.observe(this, { isSuccessful ->
            if (isSuccessful) {
                viewModel.isSending.value = false
                viewModel.isOpenAttachment.value = false
            }
        })

        viewModel.isEndedSession.observe(this, { isEnded ->
            if (isEnded) {
                ChatPopup.Builder(this)
                    .setMessage(LiveChatHelper.settings?.data?.language?.sdkSessionIsEnded)
                    .setStatus(Utils.DialogStatus.ERROR)
                    .setCallbackPositiveButtonClick { dialog ->
                        dialog.dismiss()
                        finish()
                    }
                    .build()
                    .show()
            }
        })
    }

    private fun initAttachment() {
        binding.imageViewAttachment.setOnClickListener {
            LiveChatHelper.closeKeyboard(this, it)
            if (binding.layoutAttachment.visibility == View.GONE) {
                viewModel.isOpenAttachment.value = true
                emojiPopup?.dismiss()
            } else {
                viewModel.isOpenAttachment.value = false
            }
        }

        binding.textViewAttachmentGallery.setOnClickListener {
            openGalleryForImage()
        }

        binding.textViewAttachmentVideo.setOnClickListener {
            openGalleryForVideo()
        }

        binding.textViewAttachmentDocument.setOnClickListener {
            openGalleryForDocument()
        }
    }

    private fun openGalleryForDocument() {
        if (permissionsAvailable(permissionsStorage)) {

            val mimeTypes = arrayOf(
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",  // .doc & .docx
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",  // .ppt & .pptx
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",  // .xls & .xlsx
                "application/pdf"
            )

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
                if (mimeTypes.isNotEmpty()) {
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                }
            } else {
                var mimeTypesStr = ""
                for (mimeType in mimeTypes) {
                    mimeTypesStr += "$mimeType|"
                }
                intent.type = mimeTypesStr.substring(0, mimeTypesStr.length - 1)
            }

            activityLauncher.launch(
                Intent.createChooser(
                    intent,
                    LiveChatHelper.settings?.data?.language?.sdkChooseDocument
                ),
                object : BaseActivityResult.OnActivityResult<ActivityResult> {
                    override fun onActivityResult(result: ActivityResult) {
                        if (result.resultCode == Activity.RESULT_OK) {
                            result.data?.data?.also { pathUri ->
                                dumpImageMetaData(
                                    pathUri,
                                    FileManager.APPLICATION_TYPE,
                                    FileManager.MediaType.FILES,
                                )
                            }
                        }
                    }
                })
        } else {
            ActivityCompat.requestPermissions(
                this,
                permissionsStorage,
                PERMISSION_DOCUMENT_REQUEST_CODE
            )
        }
    }

    private fun openGalleryForVideo() {
        if (permissionsAvailable(permissionsStorage)) {
            activityLauncher.launch(
                FileManager.createIntentForVideos(),
                object : BaseActivityResult.OnActivityResult<ActivityResult> {
                    override fun onActivityResult(result: ActivityResult) {
                        if (result.resultCode == Activity.RESULT_OK) {
                            result.data?.data?.also { pathUri ->
                                dumpImageMetaData(
                                    pathUri,
                                    FileManager.VIDEO_TYPE,
                                    FileManager.MediaType.VIDEOS
                                )
                            }
                        }
                    }
                })
        } else {
            ActivityCompat.requestPermissions(
                this,
                permissionsStorage,
                PERMISSION_VIDEO_REQUEST_CODE
            )
        }
    }

    private fun openGalleryForImage() {
        if (permissionsAvailable(permissionsStorage)) {
            activityLauncher.launch(
                FileManager.createIntentForImages(),
                object : BaseActivityResult.OnActivityResult<ActivityResult> {
                    override fun onActivityResult(result: ActivityResult) {
                        if (result.resultCode == Activity.RESULT_OK) {
                            result.data?.data?.also { pathUri ->
                                dumpImageMetaData(
                                    pathUri,
                                    FileManager.IMAGE_TYPE,
                                    FileManager.MediaType.IMAGES
                                )
                            }
                        }
                    }
                })
        } else {
            ActivityCompat.requestPermissions(
                this,
                permissionsStorage,
                PERMISSION_IMAGE_REQUEST_CODE
            )
        }
    }

    private fun createEmojiPopup() {
        emojiPopup =
            EmojiPopup.Builder.fromRootView(binding.layoutRoot).setOnEmojiPopupShownListener {
            }.build(binding.editTextMessage)

        binding.imageViewEmoji.setOnClickListener {
            viewModel.isOpenAttachment.value = false
            viewModel.isOpenEmojiPopup.value = !emojiPopup?.isShowing!!
            emojiPopup?.toggle()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_IMAGE_REQUEST_CODE -> {
                if (permissionsAvailable(permissionsStorage))
                    openGalleryForImage()
            }
            PERMISSION_VIDEO_REQUEST_CODE -> {
                if (permissionsAvailable(permissionsStorage))
                    openGalleryForVideo()
            }
            PERMISSION_DOCUMENT_REQUEST_CODE -> {
                if (permissionsAvailable(permissionsStorage))
                    openGalleryForDocument()
            }
        }
    }

    private fun dumpImageMetaData(uri: Uri, fileType: String, mediaType: String) {
        val contentResolver = applicationContext.contentResolver
        val cursor: Cursor? = contentResolver.query(
            uri, null, null, null, null, null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)

                if (!it.isNull(sizeIndex)) {
                    val size = it.getInt(sizeIndex) / (1024 * 1024)
                    if (size > FileManager.FILE_MAX_SIZE) {
                        ChatPopup.Builder(this)
                            .setMessage(LiveChatHelper.settings?.data?.language?.sdkFileLimit)
                            .setStatus(Utils.DialogStatus.WARNING)
                            .setCallbackPositiveButtonClick { dialog ->
                                dialog.dismiss()
                            }
                            .build()
                            .show()
                    } else {
                        val path = getFileName(uri)?.let { it1 -> getFilePathFromUri(uri, it1) }
                        path?.let { it1 -> uploadFile(displayName, it1, fileType, mediaType) }
                    }
                }
            }
        }
    }

    private fun uploadFile(name: String, path: String, fileType: String, mediaType: String) {
        val file = File(path)
        viewModel.sendMessageWithAttachment(
            file,
            fileType,
            mediaType,
            name = name
        )
    }

    private fun getFilePathFromUri(uri: Uri, fileName: String): String? {
        val downloadDirectory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)

        if (downloadDirectory?.exists() == false)
            downloadDirectory.mkdirs()

        if (!TextUtils.isEmpty(fileName)) {
            val copyFile = File("$downloadDirectory${File.separator}$fileName")
            copy(uri, copyFile)
            return copyFile.absolutePath
        }

        return null
    }

    private fun copy(uri: Uri, copyFile: File) {
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return
            val outputStream = FileOutputStream(copyFile)
            copyStream(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
        } catch (ex: Exception) {

        }
    }


    @Throws(java.lang.Exception::class, IOException::class)
    fun copyStream(input: InputStream, output: OutputStream): Int {
        val bufferSize = 1024 * 2
        val buffer = ByteArray(bufferSize)
        val `in` = BufferedInputStream(input, bufferSize)
        val out = BufferedOutputStream(output, bufferSize)
        var count = 0
        var n = 0
        try {
            while (`in`.read(buffer, 0, bufferSize).also { n = it } != -1) {
                out.write(buffer, 0, n)
                count += n
            }
            out.flush()
        } finally {
            try {
                out.close()
            } catch (e: IOException) {

            }
            try {
                `in`.close()
            } catch (e: IOException) {

            }
        }
        return count
    }

    private fun getFileName(uri: Uri): String? {
        val path = uri.path
        val cut = path?.lastIndexOf('/')
        if (cut != -1) {
            return cut?.plus(1)?.let { path.substring(it) }
        }

        return null
    }
}