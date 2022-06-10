package com.ivy.wallet.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.settings.SettingsAct
import com.ivy.wallet.domain.action.wallet.CalcIncomeExpenseAct
import com.ivy.wallet.domain.action.wallet.CalcWalletBalanceAct
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.onboarding.model.toCloseTimeRange
import com.ivy.wallet.utils.shortenAmount
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class WalletBalanceWidget: GlanceAppWidget() {

    @Composable
    override fun Content() {
        val prefs = currentState<Preferences>()
        val balance = prefs[stringPreferencesKey("balance")] ?: "0.00"
        val currency = prefs[stringPreferencesKey("currency")] ?: "USD"
        val income = prefs[stringPreferencesKey("income")] ?: "0.00"
        val expense = prefs[stringPreferencesKey("expense")] ?: "0.00"

        WalletBalanceWidgetContent(balance, currency, income, expense)
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
    lateinit var accountsAct: AccountsAct
    @Inject
    lateinit var calcIncomeExpenseAct: CalcIncomeExpenseAct
    @Inject
    lateinit var ivyContext: IvyWalletCtx

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
                GlanceAppWidgetManager(context).getGlanceIds(WalletBalanceWidget::class.java).firstOrNull()
            glanceId?.let {
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                    pref.toMutablePreferences().apply {
                        this[stringPreferencesKey("balance")] = shortenAmount(balance.toDouble())
                        this[stringPreferencesKey("currency")] = currency
                        this[stringPreferencesKey("income")] = shortenAmount(incomeExpense.income.toDouble())
                        this[stringPreferencesKey("expense")] = shortenAmount(incomeExpense.expense.toDouble())
                    }
                }
                glanceAppWidget.update(context, it)
            }
        }
    }
}