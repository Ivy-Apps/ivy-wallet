package com.ivy.legacy

import com.ivy.legacy.data.SharedPrefs
import com.ivy.legacy.utils.ioThread
import com.ivy.navigation.MainScreen
import com.ivy.navigation.Navigation
import com.ivy.navigation.OnboardingScreen
import com.ivy.data.db.IvyRoomDatabase
import javax.inject.Inject

@Deprecated("Migrate to FP Style & Actions")
class LogoutLogic @Inject constructor(
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
        navigation.navigateTo(OnboardingScreen)
    }

    suspend fun cloudLogout() {
        navigation.navigateTo(MainScreen)
    }
}
