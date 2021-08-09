package com.desk360.livechat.presentation.activity.livechat

import android.content.Intent
import com.desk360.base.presentation.addTextWatcher
import com.desk360.base.presentation.setError
import com.desk360.base.util.Utils
import com.desk360.livechat.R
import com.desk360.livechat.databinding.ActivityLoginNewChatBinding
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.presentation.activity.BaseActivity
import com.desk360.livechat.presentation.activity.BaseChatActivity

class LoginNewChatActivity : BaseActivity<ActivityLoginNewChatBinding, LoginNewChatViewModel>() {
    override fun getLayoutResId() = R.layout.activity_login_new_chat

    override fun getViewModelClass() = LoginNewChatViewModel::class.java

    override fun onResume() {
        super.onResume()
        viewModel.checkStatus()
    }

    override fun initUI() {
        binding.viewModel = viewModel

        binding.textViewLoginBg.onClick {
            createNewChat()
        }

        binding.toolbar.imageViewBack.onClick {
            LiveChatHelper.closeKeyboard(this, binding.toolbar.imageViewBack)
            onBackPressed()
        }

        binding.editTextNickname.addTextWatcher(binding.textViewNicknameError)
        binding.editTextMailAddress.addTextWatcher(binding.textViewEmailError)
        binding.editTextMessage.addTextWatcher(binding.textViewMessageError)
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
    }

    private fun createNewChat() {
        viewModel.setSessionRequest(
            binding.editTextNickname.text.toString().trim(),
            binding.editTextMailAddress.text.toString().trim(),
            binding.editTextMessage.text.toString().trim()
        )

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

        viewModel.createNewChat()
    }
}