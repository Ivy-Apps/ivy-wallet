package com.ivy.accounts
import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test

class AccountsTabKtTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = PIXEL_5
    )

    @Test
    fun accountsTabScreenshotTest() {
        paparazzi.snapshot {
            AccountsTabUITest()
        }
    }

}