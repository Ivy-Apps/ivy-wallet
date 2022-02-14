package com.ivy.wallet.logic

import com.ivy.wallet.base.ioThread
import com.ivy.wallet.persistence.IvyRoomDatabase
import com.ivy.wallet.persistence.SharedPrefs
import com.ivy.wallet.session.IvySession
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.Screen

class LogoutLogic(
    private val ivyDb: IvyRoomDatabase,
    private val ivySession: IvySession,
    private val sharedPrefs: SharedPrefs,
    private val ivyContext: IvyWalletCtx
) {
    suspend fun logout() {
        ioThread {
            ivyDb.reset()
            ivySession.logout()
            sharedPrefs.removeAll()
        }

        ivyContext.navigateTo(Screen.Onboarding)
        ivyContext.resetBackStack()
    }
}