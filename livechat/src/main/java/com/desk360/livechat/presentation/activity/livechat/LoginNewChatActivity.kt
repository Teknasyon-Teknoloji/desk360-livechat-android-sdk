package com.desk360.livechat.presentation.activity.livechat

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color.parseColor
import android.text.Editable
import android.view.ContextThemeWrapper
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import com.desk360.base.presentation.addTextWatcher
import com.desk360.base.presentation.setError
import com.desk360.base.util.Utils
import com.desk360.livechat.R
import com.desk360.livechat.data.model.chatsettings.CustomField
import com.desk360.livechat.databinding.ActivityLoginNewChatBinding
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.presentation.activity.BaseActivity
import com.desk360.livechat.presentation.activity.BaseChatActivity

class LoginNewChatActivity : BaseActivity<ActivityLoginNewChatBinding, LoginNewChatViewModel>() {
    override fun getLayoutResId() = R.layout.activity_login_new_chat

    override fun getViewModelClass() = LoginNewChatViewModel::class.java

    private lateinit var customField: LinearLayout
    private val currentEditTextList = mutableListOf<AppCompatEditText>()
    private var activeCustomField = mutableListOf<CustomField>()

    override fun onResume() {
        super.onResume()
        viewModel.checkStatus()
    }

    private val Int.px get() = (this * Resources.getSystem().displayMetrics.density).toInt()
    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun initUI() {
        binding.viewModel = viewModel

        binding.textViewLoginBg.onClick {
            createNewChat()
        }

        binding.toolbar.imageViewBack.onClick {
            LiveChatHelper.closeKeyboard(this, binding.toolbar.imageViewBack)
            onBackPressed()
        }


        customField = findViewById(R.id.custom_field_layout)
        setCustomField()


        binding.editTextNickname.addTextWatcher(binding.textViewNicknameError)
        binding.editTextMailAddress.addTextWatcher(binding.textViewEmailError)
        binding.editTextMessage.addTextWatcher(binding.textViewMessageError)

    }

    private fun setCustomField() {
        viewModel.customFieldList?.let { list ->
            list.forEach { i ->
                i.isActive?.takeIf { it }.let {
                    activeCustomField.add(i)
                    addCustomField(i)
                }
            }
        }
    }

    private fun addCustomField(field: CustomField) {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 46.px
        )
        layoutParams.setMargins(0, 0, 0, 30)
        val editText =
            AppCompatEditText(
                ContextThemeWrapper(this, R.style.CustomEditText), null, 0
            ).apply {
                hint = field.title?.toEditable()
                tag = field.key
            }
        currentEditTextList.add(editText)
        customField.addView(editText, layoutParams)
    }

    override fun initObservers() {
        viewModel.isSentOfflineMessage.observe(this, { isSuccessful ->
            if (isSuccessful) {
                finish()
                startActivity(Intent(this, SentMessageInfoActivity::class.java))
            }
        })

        viewModel.conversationId.observe(this, { conversationId ->
            if (conversationId.isNotEmpty()) {
                finish()
                startActivity(Intent(this, LiveChatActivity::class.java).apply {
                    putExtra(BaseChatActivity.EXTRA_CONVERSATION_ID, conversationId)
                })
            }
        })
        viewModel.screenModel.observe(this, { data ->
            currentEditTextList.forEach { i ->
                data?.writeMessageTextColor?.let { i.setTextColor(parseColor(it)) }
                data?.placeholderColor?.let { i.setHintTextColor(parseColor(it)) }
            }
        })
    }

    private fun createNewChat() {
        viewModel.setSessionRequest(
            binding.editTextNickname.text.toString().trim(),
            binding.editTextMailAddress.text.toString().trim(),
            binding.editTextMessage.text.toString().trim()
        )

        currentEditTextList.forEachIndexed { i, editText ->
            activeCustomField[i].value = editText.text.toString()
        }

        if (!viewModel.isNicknameValid()) {
            setError(
                binding.editTextNickname,
                binding.textViewNicknameError,
                LiveChatHelper.settings?.data?.language?.requiredMessage
            )
            binding.editTextNickname.requestFocus()
            return
        }

        if (!viewModel.isEmailAddressValid()) {
            setError(
                binding.editTextMailAddress,
                binding.textViewEmailError,
                LiveChatHelper.settings?.data?.language?.requiredMessage
            )
            binding.editTextMailAddress.requestFocus()
            return
        }

        if (!Utils.isEmailValid(binding.editTextMailAddress.text.toString().trim())) {
            setError(
                binding.editTextMailAddress,
                binding.textViewEmailError,
                LiveChatHelper.settings?.data?.language?.requiredMessage
            )
            binding.editTextMailAddress.requestFocus()
            return
        }

        if (!viewModel.isMessageValid()) {
            setError(
                binding.editTextMessage,
                binding.textViewMessageError,
                LiveChatHelper.settings?.data?.language?.requiredMessage
            )
            binding.editTextMessage.requestFocus()
            return
        }

        viewModel.createNewChat(activeCustomField)
    }
}