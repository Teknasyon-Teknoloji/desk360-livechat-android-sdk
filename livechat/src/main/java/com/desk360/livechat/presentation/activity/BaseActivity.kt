package com.desk360.livechat.presentation.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.*
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.desk360.base.domain.usecase.AutoLoginTasksImpl
import com.desk360.base.presentation.component.CustomProgressDialog
import com.desk360.base.presentation.popup.ChatPopup
import com.desk360.base.receiver.ConnectivityBroadcastReceiver
import com.desk360.base.util.Utils
import com.desk360.base.util.flowOfClick
import com.desk360.base.util.isNetworkAvailable
import com.desk360.base.util.throttleFirst
import com.desk360.livechat.BindingExt.binding
import com.desk360.livechat.R
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.presentation.viewmodel.BaseViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.net.ConnectException
import java.net.UnknownHostException

abstract class BaseActivity<VB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity(),
    LifecycleObserver {
    protected val activityLauncher: BaseActivityResult<Intent, ActivityResult> =
        BaseActivityResult.registerActivityForResult(this)

    val binding: VB by binding(getLayoutResId())

    val viewModel: VM by lazy { ViewModelProvider(this).get(getViewModelClass()) }

    val compositeDisposable = CompositeDisposable()

    val customProgressDialog = CustomProgressDialog()

    private val connectivityManager by lazy {
        applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    }

    private val connectivityCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            runOnUiThread { viewModel.setConnection(true) }
        }

        override fun onLost(network: Network) {
            runOnUiThread { viewModel.setConnection(false) }
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) = Unit

        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) =
            Unit
    }

    private val connectionReceiver = object : ConnectivityBroadcastReceiver() {
        override fun onConnectionChanged(hasConnection: Boolean) {
            viewModel.setConnection(hasConnection)
        }
    }

    @LayoutRes
    abstract fun getLayoutResId(): Int

    abstract fun getViewModelClass(): Class<VM>

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this
        viewModel.registerLifecycle(lifecycle)

        window.statusBarColor = LiveChatHelper.getStatusColor()

        registerReceivers()
        initUI()
        initObservers()

        viewModel.errorMessage.observe(this) { message ->
            if (viewModel.error.value is ConnectException || viewModel.error.value is UnknownHostException) {
                ChatPopup.Builder(this)
                    .setStatus(Utils.DialogStatus.CUSTOM)
                    .setIcon(ContextCompat.getDrawable(this, R.drawable.ic_internet))
                    .setIconBackground(ContextCompat.getDrawable(this, R.drawable.ic_warning))
                    .setAction(LiveChatHelper.settings?.data?.language?.error)
            } else {
                ChatPopup.Builder(this)
                    .setStatus(Utils.DialogStatus.ERROR)
            }.setMessage(message)
                .setCallbackPositiveButtonClick { dialog ->
                    dialog.dismiss()
                }
                .build().show()
        }

        viewModel.isProgressDialogState.observe(this) {
            when (it) {
                AutoLoginTasksImpl.LoginProgressDialogState.START -> customProgressDialog.show(
                    this,
                    "Connecting..."
                )
                AutoLoginTasksImpl.LoginProgressDialogState.STOP -> customProgressDialog.dialog.hideDialog()
                else -> {}
            }
        }

        viewModel.checkOnlineStatus()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setConnection(isNetworkAvailable())
    }

    private fun registerReceivers() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager?.registerDefaultNetworkCallback(connectivityCallback)
        } else {
            registerReceiver(
                connectionReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
    }

    private fun unRegisterReceivers() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager?.unregisterNetworkCallback(connectivityCallback)
        } else {
            unregisterReceiver(connectionReceiver)
        }
    }

    override fun onDestroy() {
        unRegisterReceivers()
        compositeDisposable.dispose()
        super.onDestroy()
    }

    abstract fun initUI()

    abstract fun initObservers()

    protected fun permissionsAvailable(permissions: Array<String>): Boolean {
        permissions.forEach { permission ->
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }

        return true
    }

    protected fun View.onClick(onSuccess: ((Any) -> Unit)) {
        lifecycleScope.launch {
            flowOfClick()
                .throttleFirst(2000)
                .flowOn(Dispatchers.Main)
                .collect {
                    onSuccess(this@onClick)
                }
        }
    }
}