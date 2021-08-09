package com.desk360.base.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

abstract class ConnectivityBroadcastReceiver : BroadcastReceiver() {
    companion object {

        fun registerToActivityAndAutoUnregister(
            activity: AppCompatActivity,
            connectionBroadcastReceiver: ConnectivityBroadcastReceiver
        ) {
            activity.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroy() {
                    activity.unregisterReceiver(connectionBroadcastReceiver)
                }

                @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
                fun onResume() {
                    activity.registerReceiver(
                        connectionBroadcastReceiver,
                        IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                    )
                }
            })
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val hasConnection =
            !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
        onConnectionChanged(hasConnection)
    }

    abstract fun onConnectionChanged(hasConnection: Boolean)
}