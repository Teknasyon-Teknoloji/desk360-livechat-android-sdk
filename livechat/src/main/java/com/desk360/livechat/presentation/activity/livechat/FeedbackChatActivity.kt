package com.desk360.livechat.presentation.activity.livechat

import android.content.Intent
import com.desk360.livechat.databinding.ActivityFeedbackChatBinding
import com.desk360.livechat.presentation.activity.BaseActivity
import com.desk360.livechat.R

class FeedbackChatActivity : BaseActivity<ActivityFeedbackChatBinding, FeedbackChatViewModel>() {
    override fun getLayoutResId() = R.layout.activity_feedback_chat

    override fun getViewModelClass() = FeedbackChatViewModel::class.java

    override fun initUI() {
        binding.viewModel = viewModel

        binding.imageViewLike.setOnClickListener {
            viewModel.feedback(FeedbackChatViewModel.Type.LIKE)
        }

        binding.imageViewUnlike.setOnClickListener {
            viewModel.feedback(FeedbackChatViewModel.Type.UNLIKE)
        }

        binding.textViewStartNewChatBg.setOnClickListener {
            if (viewModel.isRunningProgress.value != true) {
                viewModel.isRunningProgress.value = false
                startActivity(Intent(this, LoginNewChatActivity::class.java))
                finish()
            }
        }

        binding.toolbar.imageViewBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initObservers() {

    }
}