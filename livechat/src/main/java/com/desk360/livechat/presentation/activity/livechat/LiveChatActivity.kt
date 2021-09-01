package com.desk360.livechat.presentation.activity.livechat

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.desk360.base.presentation.menu.MenuAdapter
import com.desk360.base.presentation.menu.MenuBalloonFactory
import com.desk360.base.util.Utils
import com.desk360.livechat.R
import com.desk360.livechat.presentation.activity.BaseChatActivity
import com.skydoves.balloon.balloon

class LiveChatActivity : BaseChatActivity(),
    MenuAdapter.CustomViewHolder.Delegate {

    private val menuAdapter by lazy { MenuAdapter(this) }
    private val menuBalloon by balloon<MenuBalloonFactory>()

    override fun initUI() {
        super.initUI()

        binding.toolbarHeader.imageViewMenu.setOnClickListener {
            menuBalloon.showAlignBottom(it, 0, 10)
        }

        initMenu()
    }

    override fun initObservers() {
        super.initObservers()

        viewModel.isEndSessionSuccessful.observe(this, { isSuccessful ->
            if (isSuccessful) {
                startActivity(Intent(this, FeedbackChatActivity::class.java))
                finish()
            }
        })
    }

    private fun initMenu() {
        val menuRecyclerView: RecyclerView =
            menuBalloon.getContentView().findViewById(R.id.recycler_view_menu)
        menuRecyclerView.adapter = menuAdapter
        menuAdapter.addCustomItem(Utils.getLiveChatMenus(this))
    }

    override fun onMenuItemClick(item: com.desk360.base.presentation.menu.MenuItem) {
        when (item.id) {
            Utils.LiveChatMenuItem.SEND_TRANSCRIPT -> {
                startActivity(Intent(this, SentTranscriptionChatActivity::class.java))
                finish()
            }
            Utils.LiveChatMenuItem.END_CHAT -> {
                viewModel.endSession()
                menuBalloon.dismiss()
            }
        }
    }
}