package com.desk360.livechat.presentation.activity.livechat.cannedresponse.presentation

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
import com.desk360.livechat.presentation.activity.livechat.LiveChatActivity
import com.desk360.livechat.presentation.activity.livechat.LoginNewChatActivity
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.CannedResponseObject
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.model.CannedScreenType
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.presentation.adapter.CannedResponseAdapter
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.presentation.viewmodel.BaseCannedBindViewModel.Companion.ACTION_SURVEY
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.presentation.viewmodel.CannedResponseViewModel
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
        viewModel.newCannedResponse1.observe(this, { data ->
            cannedResponseListAdapter?.setData(data, true)
            viewModel.clearBindEntity()
        })

        viewModel.screenType.observe(this, { screen ->
            if (screen == CannedScreenType.OnlineScreen) {
                startActivity(Intent(this, LiveChatActivity::class.java))
                finish()
            }
            if (screen == CannedScreenType.OfflineScreen) {
                startActivity(Intent(this, LoginNewChatActivity::class.java))
                finish()
            }
            if (screen == CannedScreenType.OnBackScreen) {
                onBackPressed()
            }
            if (screen == CannedScreenType.OnSurveyInfoScreen) {
                ChatPopup.Builder(this)
                    .setStatus(Utils.DialogStatus.SUCCESS)
                    .setMessage(LiveChatHelper.settings?.data?.language?.crFeedBackSuccessTitle)
                    .setCallbackPositiveButtonClick { dialog ->
                        dialog.dismiss()
                        finish()
                    }.build().show()
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