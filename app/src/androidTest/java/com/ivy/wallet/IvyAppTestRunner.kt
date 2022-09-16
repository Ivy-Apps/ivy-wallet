package com.ivy.wallet

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.test.runner.AndroidJUnitRunner
import com.ivy.base.RootIntent
import com.ivy.data.transaction.TrnTypeOld
import com.ivy.wallet.ui.RootActivity
import com.ivy.wallet.ui.RootViewModel
import dagger.hilt.android.testing.HiltTestApplication

@Suppress("UNUSED")
class IvyAppTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, name: String?, context: Context): Application {
        IvyAndroidApp.appContext = context
        com.ivy.core.ui.temp.GlobalProvider.appContext = context
        com.ivy.core.ui.temp.GlobalProvider.rootIntent = object : RootIntent {
            override fun getIntent(context: Context): Intent =
                Intent(context, RootActivity::class.java)

            override fun addTransactionStart(context: Context, type: TrnTypeOld): Intent =
                Intent(context, RootActivity::class.java).apply {
                    putExtra(RootViewModel.EXTRA_ADD_TRANSACTION_TYPE, type)
                }
        }
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}