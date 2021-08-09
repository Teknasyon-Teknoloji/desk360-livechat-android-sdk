package com.desk360.livechat.manager

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object LiveChatFirebaseHelper {
    const val FIREBASE_NAME = "LIVE_CHAT"
    var app: FirebaseApp? = null

    var database: FirebaseDatabase? = null
        get() {
            if (field == null)
                database = Firebase.database(requireNotNull(app))

            return field
        }

    var auth: FirebaseAuth? = null
        get() {
            if (field == null)
                auth = Firebase.auth(requireNotNull(app))

            return field
        }

    val userId: String?
        get() = auth?.uid

    fun isOffline(companyId: Int) = database?.getReference("count/$companyId/online")

    fun signOut() {
        auth?.signOut()
    }
}