package com.ivy.wallet.domain.data.analytics

import java.util.*

data class AnalyticsEvent(
    val name: String,
    val sessionId: UUID,
    val id: UUID = UUID.randomUUID()
) {
    companion object {
        const val ONBOARDING_STARTED = "onboarding_started"
        const val ONBOARDING_LOGIN = "onboarding_login"
        const val ONBOARDING_PRIVACY_TC_ACCEPTED = "onboarding_privacy_tc_accepted"
        const val ONBOARDING_LOCAL_ACCOUNT = "onboarding_local_account"
        const val ONBOARDING_NAME_SET = "onboarding_name_set"
        const val ONBOARDING_CURRENCY_SET = "onboarding_currency_set"

        const val ONBOARDING_ACCOUNTS_DONE = "onboarding_accounts_done"
        const val ONBOARDING_ACCOUNTS_SKIP = "onboarding_accounts_skip"
        const val ONBOARDING_CATEGORIES_DONE = "onboarding_categories_done"
        const val ONBOARDING_CATEGORIES_SKIP = "onboarding_categories_skip"
        const val ONBOARDING_COMPLETED = "onboarding_completed"

        const val LOGIN_FROM_SETTINGS = "login_from_settings"

        const val PAYWALL_NO_REASON = "paywall_no_reason"
        const val PAYWALL_ACCOUNTS = "paywall_accounts"
        const val PAYWALL_CATEGORIES = "paywall_categories"
        const val PAYWALL_EXPORT_CSV = "paywall_export_csv"
        const val PAYWALL_PREMIUM_COLOR = "paywall_premium_color"
        const val PAYWALL_BUDGETS = "paywall_budgets"
        const val PAYWALL_LOANS = "paywall_loans"

        const val PAYWALL_CHOOSE_PLAN = "paywall_choose_plan"
        const val PAYWALL_CHOOSE_PLAN_MONTHLY = "paywall_choose_plan_monthly"
        const val PAYWALL_CHOOSE_PLAN_6MONTH = "paywall_choose_plan_6month"
        const val PAYWALL_CHOOSE_PLAN_YEARLY = "paywall_choose_plan_yearly"
        const val PAYWALL_CHOOSE_PLAN_LIFETIME = "paywall_choose_plan_lifetime"

        const val PAYWALL_START_BUY = "paywall_start_buy"
        const val PAYWALL_START_BUY_MONTHLY = "paywall_start_buy_monthly"
        const val PAYWALL_START_BUY_6MONTH = "paywall_start_buy_6month"
        const val PAYWALL_START_BUY_YEARLY = "paywall_start_buy_6_yearly"
        const val PAYWALL_START_BUY_LIFETIME = "paywall_start_buy_lifetime"

        const val PAYWALL_ACTIVE_PREMIUM = "paywall_active_premium"
    }
}
