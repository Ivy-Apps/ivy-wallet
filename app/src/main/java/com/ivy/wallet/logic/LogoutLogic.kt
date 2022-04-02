package com.ivy.wallet.logic

import com.ivy.design.navigation.Navigation
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.persistence.IvyRoomDatabase
import com.ivy.wallet.persistence.SharedPrefs
import com.ivy.wallet.session.IvySession
import com.ivy.wallet.ui.Onboarding

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