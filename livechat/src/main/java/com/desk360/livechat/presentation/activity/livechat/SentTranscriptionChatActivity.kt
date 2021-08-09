package com.desk360.livechat.presentation.activity.livechat

import com.desk360.livechat.R
import com.desk360.livechat.databinding.ActivitySentTranscriptionChatBinding
import com.desk360.livechat.presentation.activity.BaseActivity

class SentTranscriptionChatActivity :
    BaseActivity<ActivitySentTranscriptionChatBinding, SentTranscriptionViewModel>() {

    override fun getLayoutResId() = R.layout.activity_sent_transcription_chat

    override fun getViewModelClass() = SentTranscriptionViewModel::class.java

    override fun initUI() {
        binding.viewModel = viewModel
    }

    override fun initObservers() {

    }
}