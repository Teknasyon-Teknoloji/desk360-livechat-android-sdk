package com.desk360.base.domain.listener

import com.desk360.base.domain.mapper.FirebaseMapper
import com.desk360.base.domain.usecase.BaseFirebaseUseCase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BaseValueEventListener<Model, Entity>(
    private val mapper: FirebaseMapper<Entity, Model>?,
    private val callback: BaseFirebaseUseCase.FirebaseDatabaseRepositoryCallback<Model>?
) : ValueEventListener {
    override fun onDataChange(dataSnapshot: DataSnapshot) {
        val data = mapper?.map(dataSnapshot)
        callback?.onSuccess(data)
    }

    override fun onCancelled(databaseError: DatabaseError) {
        callback?.onError(databaseError.toException().fillInStackTrace())
    }
}