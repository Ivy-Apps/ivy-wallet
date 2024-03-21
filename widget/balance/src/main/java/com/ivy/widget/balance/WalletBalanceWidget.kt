package com.ivy.widget.balance

import android.appwidget.AppWidgetManager
import android.content.Context
import android.icu.text.DecimalFormat
import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.ivy.base.model.TransactionType
import com.ivy.domain.AppStarter
import com.ivy.base.legacy.SharedPrefs
import com.ivy.legacy.data.model.toCloseTimeRange
import com.ivy.legacy.utils.shortenAmount
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.settings.SettingsAct
import com.ivy.wallet.domain.action.wallet.CalcIncomeExpenseAct
import com.ivy.wallet.domain.action.wallet.CalcWalletBalanceAct
import com.ivy.widgets.WidgetBase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

const val THOUSAND = 1000

class WalletBalanceWidget(
    private val getAppStarter: () -> AppStarter,
) : GlanceAppWidget() {
    @Composable
    fun formatBalance(balance: String): String {
        val formattedBalance = remember(balance) {
            val balanceDouble = balance.toDouble()
            if (Math.abs(balanceDouble) < THOUSAND) {
                DecimalFormat("###,###.##").format(balanceDouble)
            } else {
                shortenAmount(balanceDouble)
            }
        }
        return formattedBalance
    }
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val appLocked = prefs[booleanPreferencesKey("appLocked")] ?: false
            val balance = prefs[stringPreferencesKey("balance")] ?: "0.00"
            val currency = prefs[stringPreferencesKey("currency")] ?: "USD"
            val income = prefs[stringPreferencesKey("income")] ?: "0.00"
            val expense = prefs[stringPreferencesKey("expense")] ?: "0.00"

            WalletBalanceWidgetContent(
                appLocked = appLocked,
                balance = formatBalance(balance),
                currency = currency,
                income = income,
                expense = expense,
                onIncomeClick = {
                    getAppStarter().addTransactionStart(TransactionType.INCOME)
                },
                onExpenseClick = {
                    getAppStarter().addTransactionStart(TransactionType.EXPENSE)
                },
                onTransferClick = {
                    getAppStarter().addTransactionStart(TransactionType.TRANSFER)
                },
                onWidgetClick = {
                    getAppStarter().defaultStart()
                },
            )
        }
    }
}

@Keep
@AndroidEntryPoint
class WalletBalanceWidgetReceiver : GlanceAppWidgetReceiver() {
    companion object {
        fun updateBroadcast(context: Context) {
            WidgetBase.updateBroadcast(context, WalletBalanceWidgetReceiver::class.java)
        }
    }

    override val glanceAppWidget: GlanceAppWidget = WalletBalanceWidget(
        getAppStarter = { appStarter }
    )
    private val coroutineScope = MainScope()

    @Inject
    lateinit var appStarter: AppStarter

    @Inject
    lateinit var walletBalanceAct: CalcWalletBalanceAct

    @Inject
    lateinit var settingsAct: SettingsAct

    @Inject
    lateinit var accountsAct: AccountsAct

    @Inject
    lateinit var calcIncomeExpenseAct: CalcIncomeExpenseAct

    @Inject
    lateinit var ivyContext: com.ivy.legacy.IvyWalletCtx

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
            val appLocked = com.ivy.legacy.utils.ioThread {
                sharedPrefs.getBoolean(
                    SharedPrefs.APP_LOCK_ENABLED,
                    false
                )
            }
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
                        this[stringPreferencesKey("balance")] =
                            com.ivy.legacy.utils.shortenAmount(balance.toDouble())
                        this[stringPreferencesKey("currency")] = currency
                        this[stringPreferencesKey("income")] =
                            com.ivy.legacy.utils.shortenAmount(incomeExpense.income.toDouble())
                        this[stringPreferencesKey("expense")] =
                            com.ivy.legacy.utils.shortenAmount(incomeExpense.expense.toDouble())
                    }
                }
                glanceAppWidget.update(context, it)
            }
        }
    }
}
