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
            if (field != null)
                return field

            val fbApp = app ?: return null

            field = Firebase.database(fbApp)

            return field
        }

    var auth: FirebaseAuth? = null
        get() {
            if (field != null)
                return field

            val fbapp = app ?: return null

            field = Firebase.auth(fbapp)

            return field
        }

    val userId: String?
        get() = auth?.uid

    fun isOffline(companyId: Int,applicationId: Int) = database?.getReference("count/$companyId/$applicationId/online")

    fun signOut() {
        auth?.signOut()
    }
}