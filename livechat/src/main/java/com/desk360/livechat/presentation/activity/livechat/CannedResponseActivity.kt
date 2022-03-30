package com.desk360.livechat.presentation.activity.livechat

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.desk360.base.presentation.menu.MenuAdapter
import com.desk360.base.presentation.menu.MenuBalloonFactory
import com.desk360.base.presentation.menu.MenuItem
import com.desk360.base.presentation.popup.ChatPopup
import com.desk360.base.util.Utils
import com.desk360.livechat.R
import com.desk360.livechat.databinding.ActivityCannedResponseBinding
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.presentation.activity.BaseActivity
import com.desk360.livechat.presentation.activity.BaseChatActivity
import com.desk360.livechat.presentation.activity.livechat.LoginNewChatActivity.Companion.EXTRA_PATH_ID
import com.desk360.livechat.data.model.cannedresponse.CannedScreenType
import com.desk360.livechat.presentation.adapter.CannedResponseAdapter
import com.desk360.livechat.presentation.activity.livechat.CannedResponseViewModel.Companion.ACTION_SURVEY
import com.skydoves.balloon.balloon
import kotlinx.coroutines.*

class CannedResponseActivity :
    BaseActivity<ActivityCannedResponseBinding, CannedResponseViewModel>(),
    MenuAdapter.CustomViewHolder.Delegate {

    private val headerMenu by balloon<MenuBalloonFactory>()
    private val menuAdapter by lazy { MenuAdapter(this) }

    private var cannedResponseListAdapter: CannedResponseAdapter? = null

    private var linearLayoutManager: LinearLayoutManager? = null


    override fun getLayoutResId(): Int = R.layout.activity_canned_response

    override fun getViewModelClass() = CannedResponseViewModel::class.java

    override fun initUI() {
        binding.viewModel = viewModel
        binding.toolbarCannedHeader.imageViewMenu.setOnClickListener {
            headerMenu.showAlignBottom(it, 0, 10)
        }
        binding.toolbarCannedHeader.imageViewBack.setOnClickListener {
            onBackPressed()
        }
        initHeaderMenu()
        initAdapter()
    }

    private fun initAdapter() {
        cannedResponseListAdapter = CannedResponseAdapter { action, id, type ->
            if (type == ACTION_SURVEY && (id == 1 || id == 2)) viewModel.surveyFeedBackUseCase(id)
            else {
                cannedResponseListAdapter?.updateClickable(id)
                viewModel.writeActionMessage(action, id, type)
                action?.let {
                    viewModel.actionCannedResponse(it)
                } ?: viewModel.actionCannedResponseMenu(type)
            }
        }
        linearLayoutManager =
            LinearLayoutManager(this@CannedResponseActivity, LinearLayoutManager.VERTICAL, false)

        binding.recyclerViewMessageList.apply {
            adapter = cannedResponseListAdapter
            layoutManager = linearLayoutManager
            setHasFixedSize(false)
        }
        binding.recyclerViewMessageList.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom)
                binding.recyclerViewMessageList.postDelayed({
                    cannedResponseListAdapter?.itemCount?.let { count ->
                        binding.recyclerViewMessageList.smoothScrollToPosition(count)
                    }
                }, 2000)
        }
        viewModel.startCannedResponse()
    }

    private fun initHeaderMenu() {
        val menuRecyclerView: RecyclerView =
            headerMenu.getContentView().findViewById(R.id.recycler_view_menu)
        menuRecyclerView.adapter = menuAdapter
        menuAdapter.addCustomItem(Utils.getLiveChatMenus(this))
    }

    override fun initObservers() {
        viewModel.newCannedResponse.observe(this, {
            cannedResponseListAdapter?.setData(it, false)
            viewModel.clearBindEntity()
            binding.recyclerViewMessageList.postDelayed({
                cannedResponseListAdapter?.itemCount?.let { count ->
                    binding.recyclerViewMessageList.smoothScrollToPosition(count)
                }
            }, 200)
        })
        viewModel.startCannedResponse.observe(this, { data ->
            cannedResponseListAdapter?.setData(data, true)
            viewModel.clearBindEntity()
        })

        viewModel.conversationDeskId.observe(this, { conversationId ->
            if (conversationId.isNotEmpty()) {
                finish()
                startActivity(Intent(this, LiveChatActivity::class.java).apply {
                    putExtra(BaseChatActivity.EXTRA_CONVERSATION_ID, conversationId)
                })
            }
        })
        viewModel.screenType.observe(this, { screen ->
            when (screen) {
                is CannedScreenType.LoginScreen -> {
                    val intent = Intent(this, LoginNewChatActivity::class.java)
                    screen.pathId?.let {
                        intent.putExtra(EXTRA_PATH_ID, true)
                    }
                    startActivity(intent)
                    finish()
                }
                is CannedScreenType.OnSurveyInfoScreen -> {
                    ChatPopup.Builder(this)
                        .setStatus(Utils.DialogStatus.SUCCESS)
                        .setMessage(LiveChatHelper.settings?.data?.language?.crFeedBackSuccessTitle)
                        .setCallbackPositiveButtonClick { dialog ->
                            dialog.dismiss()
                            finish()
                        }.build().show()
                }
                is CannedScreenType.OnBackScreen -> {
                    onBackPressed()
                }
            }
        })
    }

    override fun onBackPressed() {
        viewModel.cannedResponseUseCase()
        super.onBackPressed()
    }

    override fun onMenuItemClick(item: MenuItem) {
        when (item.id) {
            Utils.LiveChatMenuItem.END_CHAT -> {
                onBackPressed()
            }
        }
    }
}