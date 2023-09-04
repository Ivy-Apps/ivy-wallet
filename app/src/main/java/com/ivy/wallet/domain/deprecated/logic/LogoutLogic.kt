package com.ivy.wallet.domain.deprecated.logic

import com.ivy.frp.view.navigation.Navigation
import com.ivy.wallet.io.persistence.IvyRoomDatabase
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.ui.Main
import com.ivy.wallet.ui.Onboarding
import com.ivy.wallet.utils.ioThread

@Deprecated("Migrate to FP Style & Actions")
class LogoutLogic(
    private val ivyDb: IvyRoomDatabase,
    private val sharedPrefs: SharedPrefs,
    private val navigation: Navigation
) {
    suspend fun logout() {
        ioThread {
            ivyDb.reset()
            sharedPrefs.removeAll()
        }

        navigation.resetBackStack()
        navigation.navigateTo(Onboarding)
    }

    suspend fun cloudLogout() {
        navigation.navigateTo(Main)
    }
}
