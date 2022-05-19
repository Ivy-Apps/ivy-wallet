package com.ivy.wallet.domain.deprecated.logic

import com.android.billingclient.api.Purchase
import com.ivy.frp.view.navigation.Navigation
import com.ivy.wallet.BuildConfig
import com.ivy.wallet.Constants
import com.ivy.wallet.android.billing.IvyBilling
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.BudgetDao
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.io.persistence.dao.LoanDao
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.Paywall
import com.ivy.wallet.ui.paywall.PaywallReason
import com.ivy.wallet.utils.ioThread

@Deprecated("Migrate to FP Style & Actions")
class PaywallLogic(
    private val ivyBilling: IvyBilling,
    private val ivyContext: IvyWalletCtx,
    private val navigation: Navigation,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val budgetDao: BudgetDao,
    private val loanDao: LoanDao
) {

    suspend fun protectQuotaExceededWithPaywall(
        onPaywallHit: () -> Unit = {},
        action: suspend () -> Unit
    ) {
        val paywallReason = checkPaywall {
            paywallHitQuotaExceeded()
        }

        if (paywallReason != null) {
            onPaywallHit()
            navigation.navigateTo(
                Paywall(
                    paywallReason = paywallReason
                )
            )
        } else {
            action()
        }
    }

    private suspend fun paywallHitQuotaExceeded(): PaywallReason? {
        return ioThread {
            val accounts = accountDao.findAll()
            if (accounts.size > Constants.FREE_ACCOUNTS) {
                return@ioThread PaywallReason.ACCOUNTS
            }

            val categories = categoryDao.findAll()
            if (categories.size > Constants.FREE_CATEGORIES) {
                return@ioThread PaywallReason.CATEGORIES
            }

            val budgets = budgetDao.findAll()
            if (budgets.size > Constants.FREE_BUDGETS) {
                return@ioThread PaywallReason.BUDGETS
            }

            val loans = loanDao.findAll()
            if (loans.size > Constants.FREE_LOANS) {
                return@ioThread PaywallReason.LOANS
            }

            null
        }

    }

    suspend fun protectAddWithPaywall(
        addAccount: Boolean = false,
        addCategory: Boolean = false,
        addBudget: Boolean = false,
        addLoan: Boolean = false,
        action: suspend () -> Unit
    ) {
        val paywallReason = checkPaywall {
            paywallHitAddItem(
                addAccount = addAccount,
                addCategory = addCategory,
                addBudget = addBudget,
                addLoan = addLoan,
            )
        }

        if (paywallReason != null) {
            navigation.navigateTo(
                Paywall(
                    paywallReason = paywallReason
                )
            )
        } else {
            action()
        }
    }

    private suspend fun checkPaywall(
        paywallHitDefinition: suspend () -> PaywallReason?
    ): PaywallReason? {
        if (BuildConfig.DEBUG && !Constants.ENABLE_PAYWALL_ON_DEBUG) {
            return null
        }

        if (ivyContext.isPremium) {
            return null
        }

        return paywallHitDefinition()
    }

    private suspend fun paywallHitAddItem(
        addAccount: Boolean,
        addCategory: Boolean,
        addBudget: Boolean,
        addLoan: Boolean
    ): PaywallReason? {
        return ioThread {
            if (addAccount) {
                val accountsCount = accountDao.findAll().size + 1 //+1 for the account being added
                if (accountsCount > Constants.FREE_ACCOUNTS) {
                    return@ioThread PaywallReason.ACCOUNTS
                }
            }

            if (addCategory) {
                val categoriesCount =
                    categoryDao.findAll().size + 1 //+1 for the category being added
                if (categoriesCount > Constants.FREE_CATEGORIES) {
                    return@ioThread PaywallReason.CATEGORIES
                }
            }

            if (addBudget) {
                val budgetsCount =
                    budgetDao.findAll().size + 1 //+1 for the item being added
                if (budgetsCount > Constants.FREE_BUDGETS) {
                    return@ioThread PaywallReason.BUDGETS
                }
            }

            if (addLoan) {
                val loansCount =
                    loanDao.findAll().size + 1 //+1 for the item being added
                if (loansCount > Constants.FREE_LOANS) {
                    return@ioThread PaywallReason.LOANS
                }
            }

            null
        }
    }


    suspend fun processPurchases(
        purchases: List<Purchase>,
        onActivePurchase: (Purchase) -> Unit = {}
    ) {
        for (purchase in purchases) {
            ivyBilling.checkPremium(
                purchase = purchase,
                onActivatePremium = {
                    ivyContext.isPremium = true
                    onActivePurchase(it)
                }
            )
        }
    }
}