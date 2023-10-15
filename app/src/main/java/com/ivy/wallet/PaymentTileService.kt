package com.ivy.wallet

import android.content.Intent
import android.service.quicksettings.TileService
import timber.log.Timber

class PaymentTileService : TileService() {
    // Called when the user adds your tile.
    override fun onTileAdded() {
        super.onTileAdded()
    }

    // Called when your app can update your tile.
    override fun onStartListening() {
        super.onStartListening()
    }

    // Called when your app can no longer update your tile.
    override fun onStopListening() {
        super.onStopListening()
    }

    // Called when the user taps on your tile in an active or inactive state.
    override fun onClick() {
        super.onClick()

        try {
            val i = Intent(applicationContext, RootActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivityAndCollapse(i)
        } catch (e: Exception) {
            Timber.tag("debug").d("Exception %s", e.toString())
        }
    }

    // Called when the user removes your tile.
    override fun onTileRemoved() {
        super.onTileRemoved()
    }
}