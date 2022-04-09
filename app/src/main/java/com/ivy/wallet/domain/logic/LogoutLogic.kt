package com.ivy.wallet.domain.logic

import com.ivy.design.navigation.Navigation
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.persistence.IvyRoomDatabase
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.ui.Onboarding
import com.ivy.wallet.utils.ioThread

class LogoutLogic(
    private val ivyDb: IvyRoomDatabase,
    private val ivySession: IvySession,
    private val sharedPrefs: SharedPrefs,
    private val navigation: Navigation
) {
    suspend fun logout() {
        ioThread {
            ivyDb.reset()
            ivySession.logout()
            sharedPrefs.removeAll()
        }

        navigation.navigateTo(Onboarding)
        navigation.resetBackStack()
    }
}