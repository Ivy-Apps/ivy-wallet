package com.ivy.wallet

import android.content.Context
import android.content.Intent
import com.ivy.base.model.TransactionType
import com.ivy.domain.AppStarter
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class IvyAppStarter @Inject constructor(
    @ApplicationContext
    private val context: Context
) : AppStarter {

    override fun getRootIntent(): Intent {
        return Intent(context, RootActivity::class.java)
    }

    override fun defaultStartIntent(): Intent {
        return getRootIntent().apply {
            applyWidgetStartFlags()
        }
    }

    override fun addTransactionStartIntent(type: TransactionType): Intent {
        return getRootIntent().apply {
            // A distinct action per type keeps the widgets' PendingIntents apart,
            // because Intent.filterEquals() ignores extras.
            action = "$ACTION_ADD_TRANSACTION_PREFIX${type.name}"
            putExtra(RootViewModel.EXTRA_ADD_TRANSACTION_TYPE, type)
            applyWidgetStartFlags()
        }
    }

    private fun Intent.applyWidgetStartFlags() {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    private companion object {
        const val ACTION_ADD_TRANSACTION_PREFIX = "com.ivy.wallet.ACTION_ADD_TRANSACTION_"
    }
}
