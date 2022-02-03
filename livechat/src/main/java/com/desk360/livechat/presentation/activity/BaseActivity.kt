package com.desk360.livechat.presentation.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
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
import com.jakewharton.rxbinding2.view.RxView
import com.desk360.base.receiver.ConnectivityBroadcastReceiver
import com.desk360.base.util.Utils
import com.desk360.base.presentation.popup.ChatPopup
import com.desk360.livechat.BindingExt.binding
import com.desk360.livechat.R
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.presentation.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

abstract class BaseActivity<VB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity(), LifecycleObserver {
    protected val activityLauncher: BaseActivityResult<Intent, ActivityResult> =
        BaseActivityResult.registerActivityForResult(this)

    val binding: VB by binding(getLayoutResId())
    val viewModel: VM by lazy {
        ViewModelProvider(this).get(getViewModelClass())
    }

    val compositeDisposable = CompositeDisposable()

    @LayoutRes
    abstract fun getLayoutResId(): Int

    abstract fun getViewModelClass(): Class<VM>

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this
        viewModel.registerLifecycle(lifecycle)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = LiveChatHelper.getStatusColor()
        }

        registerReceivers()
        initUI()
        initObservers()

        viewModel.errorMessage.observe(this, { message ->
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
        })
        viewModel.checkOnlineStatus()
    }

    private fun registerReceivers() {
        ConnectivityBroadcastReceiver.registerToActivityAndAutoUnregister(this, object :
            ConnectivityBroadcastReceiver() {
            override fun onConnectionChanged(hasConnection: Boolean) {
                viewModel.setConnection(hasConnection)
            }
        })
    }

    override fun onDestroy() {
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
        val disposable = RxView.clicks(this)
            .throttleFirst(2000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .subscribe(onSuccess)
        compositeDisposable.add(disposable)
    }
}