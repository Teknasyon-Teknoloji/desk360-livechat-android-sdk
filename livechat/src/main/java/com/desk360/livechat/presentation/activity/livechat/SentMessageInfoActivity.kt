package com.desk360.livechat.presentation.activity.livechat

import android.content.Intent
import com.desk360.livechat.R
import com.desk360.livechat.databinding.ActivitySentMessageInfoBinding
import com.desk360.livechat.presentation.activity.BaseActivity

class SentMessageInfoActivity :
    BaseActivity<ActivitySentMessageInfoBinding, SentMessageInfoViewModel>() {

    override fun getLayoutResId() = R.layout.activity_sent_message_info

    override fun getViewModelClass() = SentMessageInfoViewModel::class.java

    override fun initUI() {
        binding.viewModel = viewModel

        binding.textViewStartNewChatBg.setOnClickListener {
            startActivity(Intent(this, LoginNewChatActivity::class.java))
            finish()
        }

        binding.toolbar.imageViewBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initObservers() {

    }
}