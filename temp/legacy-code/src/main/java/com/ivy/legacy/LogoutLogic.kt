package com.ivy.legacy

import com.ivy.base.legacy.SharedPrefs
import com.ivy.data.DataObserver
import com.ivy.data.DataWriteEvent
import com.ivy.data.db.IvyRoomDatabase
import com.ivy.legacy.utils.ioThread
import com.ivy.navigation.MainScreen
import com.ivy.navigation.Navigation
import com.ivy.navigation.OnboardingScreen
import javax.inject.Inject

@Deprecated("Migrate to FP Style & Actions")
class LogoutLogic @Inject constructor(
    private val ivyDb: IvyRoomDatabase,
    private val sharedPrefs: SharedPrefs,
    private val navigation: Navigation,
    private val dataObserver: DataObserver,
) {
    suspend fun logout() {
        ioThread {
            ivyDb.reset()
            sharedPrefs.removeAll()
        }

        dataObserver.post(DataWriteEvent.AllDataChange)
        navigation.resetBackStack()
        navigation.navigateTo(OnboardingScreen)
    }

    suspend fun cloudLogout() {
        navigation.navigateTo(MainScreen)
    }
}
