package com.ivy.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.ivy.base.toCloseTimeRange
import com.ivy.wallet.domain.action.account.AccountsActOld
import com.ivy.wallet.domain.action.settings.SettingsAct
import com.ivy.wallet.domain.action.wallet.CalcIncomeExpenseAct
import com.ivy.wallet.domain.action.wallet.CalcWalletBalanceAct
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.utils.ioThread
import com.ivy.wallet.utils.shortenAmount
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class WalletBalanceWidget : GlanceAppWidget() {

    @Composable
    override fun Content() {
        val prefs = currentState<Preferences>()
        val appLocked = prefs[booleanPreferencesKey("appLocked")] ?: false
        val balance = prefs[stringPreferencesKey("balance")] ?: "0.00"
        val currency = prefs[stringPreferencesKey("currency")] ?: "USD"
        val income = prefs[stringPreferencesKey("income")] ?: "0.00"
        val expense = prefs[stringPreferencesKey("expense")] ?: "0.00"

        WalletBalanceWidgetContent(appLocked, balance, currency, income, expense)
    }

}

@AndroidEntryPoint
class WalletBalanceReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = WalletBalanceWidget()
    private val coroutineScope = MainScope()

    @Inject
    lateinit var walletBalanceAct: CalcWalletBalanceAct

    @Inject
    lateinit var settingsAct: SettingsAct

    @Inject
    lateinit var accountsAct: AccountsActOld

    @Inject
    lateinit var calcIncomeExpenseAct: CalcIncomeExpenseAct

    @Inject
    lateinit var ivyContext: com.ivy.core.ui.temp.IvyWalletCtx

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        updateData(context)
    }

    private fun updateData(context: Context) {
        coroutineScope.launch {
            val settings = settingsAct(Unit)
            val appLocked = ioThread { sharedPrefs.getBoolean(SharedPrefs.APP_LOCK_ENABLED, false) }
            val currency = settings.baseCurrency
            val balance = walletBalanceAct(CalcWalletBalanceAct.Input(baseCurrency = currency))
            val accounts = accountsAct(Unit)
            val period = ivyContext.selectedPeriod
            val incomeExpense = calcIncomeExpenseAct(
                CalcIncomeExpenseAct.Input(
                    baseCurrency = settings.baseCurrency,
                    accounts = accounts,
                    range = period.toRange(ivyContext.startDayOfMonth).toCloseTimeRange()
                )
            )

            val glanceId =
                GlanceAppWidgetManager(context).getGlanceIds(WalletBalanceWidget::class.java)
                    .firstOrNull()
            glanceId?.let {
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                    pref.toMutablePreferences().apply {
                        this[booleanPreferencesKey("appLocked")] = appLocked
                        this[stringPreferencesKey("balance")] = shortenAmount(balance.toDouble())
                        this[stringPreferencesKey("currency")] = currency
                        this[stringPreferencesKey("income")] =
                            shortenAmount(incomeExpense.income.toDouble())
                        this[stringPreferencesKey("expense")] =
                            shortenAmount(incomeExpense.expense.toDouble())
                    }
                }
                glanceAppWidget.update(context, it)
            }
        }
    }
}