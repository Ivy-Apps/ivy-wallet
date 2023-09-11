package com.ivy.wallet

import android.content.Context
import android.content.Intent
import com.ivy.core.AppStarter
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.ui.RootActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class IvyAppStarter @Inject constructor(
    @ApplicationContext
    private val context: Context
) : AppStarter {

    override fun getRootIntent(): Intent {
        return RootActivity.getIntent(context = context)
    }

    override fun defaultStart() {
        context.startActivity(
            getRootIntent().apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }

    override fun addTransactionStart(type: TransactionType) {
        context.startActivity(
            RootActivity.addTransactionStart(
                context = context,
                type = type
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }
}