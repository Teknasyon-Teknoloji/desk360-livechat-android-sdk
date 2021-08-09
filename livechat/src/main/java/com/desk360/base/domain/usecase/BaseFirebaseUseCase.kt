package com.desk360.base.domain.usecase

import com.google.firebase.database.DatabaseReference
import com.desk360.base.domain.listener.BaseValueEventListener
import com.desk360.base.domain.mapper.FirebaseMapper
import com.desk360.livechat.manager.LiveChatFirebaseHelper

abstract class BaseFirebaseUseCase<Model>(mapper: FirebaseMapper<*, Model>) {
    private var databaseReference: DatabaseReference? = null
    private var listener: BaseValueEventListener<Model, *>? = null
    private val mapper: FirebaseMapper<*, Model>
    protected abstract val rootNode: String

    open fun execute(
        onSuccess: (result: Model?) -> (Unit),
        onError: (th: Throwable?) -> (Unit)
    ) {
        addListener(object : FirebaseDatabaseRepositoryCallback<Model> {
            override fun onSuccess(result: Model?) {
                onSuccess(result)
            }

            override fun onError(th: Throwable?) {
                onError(th)
            }
        })
    }

    private fun addListener(callBack: FirebaseDatabaseRepositoryCallback<Model>) {
        listener = BaseValueEventListener(mapper, callBack)
        listener?.let {
            databaseReference?.addValueEventListener(it)
        }
    }

    fun removeListener() {
        listener?.let { databaseReference?.removeEventListener(it) }
    }

    interface FirebaseDatabaseRepositoryCallback<T> {
        fun onSuccess(result: T?)
        fun onError(th: Throwable?)
    }

    init {
        databaseReference = LiveChatFirebaseHelper.database?.getReference(rootNode)
        this.mapper = mapper
    }
}