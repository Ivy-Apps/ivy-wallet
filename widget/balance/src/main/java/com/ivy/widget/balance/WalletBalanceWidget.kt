package com.ivy.widget.balance

import android.appwidget.AppWidgetManager
import android.content.Context
import android.icu.text.DecimalFormat
import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.ivy.base.legacy.SharedPrefs
import com.ivy.base.model.TransactionType
import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import com.ivy.domain.AppStarter
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
import kotlin.math.abs

const val THOUSAND = 1000

object PrefsKey {
    const val APP_LOCKED = "appLocked"
    const val BALANCE = "balance_v2"
    const val CURRENCY = "currency"
    const val INCOME = "income_v2"
    const val EXPENSE = "expense_v2"
}

class WalletBalanceWidget(
    private val getAppStarter: () -> AppStarter,
) : GlanceAppWidget() {
    @Composable
    fun formatBalance(balance: Double): String {
        val formattedBalance = remember(balance) {
            if (abs(balance) < THOUSAND) {
                DecimalFormat("###,###.##").format(balance)
            } else {
                shortenAmount(balance)
            }
        }
        return formattedBalance
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val appLocked = prefs[booleanPreferencesKey(PrefsKey.APP_LOCKED)] ?: false
            val balance = prefs[doublePreferencesKey(PrefsKey.BALANCE)] ?: 0.00
            val currency = prefs[stringPreferencesKey(PrefsKey.CURRENCY)] ?: "USD"
            val income = prefs[doublePreferencesKey(PrefsKey.INCOME)] ?: 0.00
            val expense = prefs[doublePreferencesKey(PrefsKey.EXPENSE)] ?: 0.00

            WalletBalanceWidgetContent(
                appLocked = appLocked,
                balance = formatBalance(balance),
                currency = currency,
                income = shortenAmount(income),
                expense = shortenAmount(expense),
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

    @Inject
    lateinit var timeProvider: TimeProvider

    @Inject
    lateinit var timeConverter: TimeConverter

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
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
                    range = period.toRange(
                        startDateOfMonth = ivyContext.startDayOfMonth,
                        timeConverter = timeConverter,
                        timeProvider = timeProvider,

                        ).toCloseTimeRange()
                )
            )

            GlanceAppWidgetManager(context).getGlanceIds(WalletBalanceWidget::class.java)
                .forEach {
                    updateAppWidgetState(
                        context,
                        PreferencesGlanceStateDefinition,
                        it
                    ) { pref ->
                        pref.toMutablePreferences().apply {
                            this[booleanPreferencesKey(PrefsKey.APP_LOCKED)] = appLocked
                            this[doublePreferencesKey(PrefsKey.BALANCE)] =
                                balance.toDouble()
                            this[stringPreferencesKey(PrefsKey.CURRENCY)] = currency
                            this[doublePreferencesKey(PrefsKey.INCOME)] =
                                incomeExpense.income.toDouble()
                            this[doublePreferencesKey(PrefsKey.EXPENSE)] =
                                incomeExpense.expense.toDouble()
                        }
                    }
                    glanceAppWidget.update(context, it)
                }
        }
    }
}
