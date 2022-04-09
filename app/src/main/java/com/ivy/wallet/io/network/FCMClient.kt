package com.ivy.wallet.io.network

import com.google.firebase.messaging.FirebaseMessaging
import com.ivy.wallet.persistence.SharedPrefs
import kotlinx.coroutines.tasks.await

class FCMClient(private val sharedPrefs: SharedPrefs) {
    companion object {
        const val NA = "n/a"
    }

    suspend fun fcmToken(): String {
        return try {
            sharedPrefs.getString(SharedPrefs.FCM_TOKEN, null)
                ?: fetchFCMTokenFromServer()
        } catch (e: Exception) {
            e.printStackTrace()
            NA
        }
    }

    private suspend fun fetchFCMTokenFromServer(): String {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            e.printStackTrace()
            NA
        }
    }
}