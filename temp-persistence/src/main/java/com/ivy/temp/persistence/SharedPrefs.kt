package com.ivy.wallet.io.persistence

import android.content.Context
import com.google.gson.Gson
import com.ivy.common.epochSecondToDateTime
import com.ivy.common.toEpochSeconds
import java.time.LocalDateTime

/**
 * Created by iliyan on 13.03.18.
 */
class SharedPrefs(appContext: Context) {
    companion object {
        private const val PREFS_FILENAME = "ivy_wallet_prefs"

        const val ONBOARDING_COMPLETED = "onboarding_completed"

        const val FCM_TOKEN = "fcm_token"

        //Sync
        const val LAST_SYNC_DATE_CATEGORIES = "last_sync_date_categories"
        const val LAST_SYNC_DATE_BUDGETS = "last_sync_date_budgets"
        const val LAST_SYNC_DATE_LOANS = "last_sync_date_loans"
        const val LAST_SYNC_DATE_LOAN_RECORDS = "last_sync_date_loan_records"
        const val LAST_SYNC_DATE_ACCOUNTS = "last_sync_date_accounts"
        const val LAST_SYNC_DATE_TRANSACTIONS = "last_sync_date_transactions"
        const val LAST_SYNC_DATE_PLANNED_PAYMENTS = "last_sync_date_planned_payments"

        //-----------------------------------------------------------------------------

        //Analytics
        const val ANALYTICS_SESSION_ID = "analytics_session_id"
        //------------------------------------------------------------------------------------------

        //-------------------------------------- UX ------------------------------------------------
        const val LAST_SELECTED_ACCOUNT_ID = "last_selected_account_id"
        //-------------------------------------- UX ------------------------------------------------

        const val SESSION_USER_ID = "session_user_id"
        const val SESSION_AUTH_TOKEN = "session_auth_token"


        //-------------------------------- Bank Integrations temp ----------------------------------
        const val ENABLE_BANK_SYNC = "enable_bank_sync"
        //-------------------------------- Bank Integrations temp ----------------------------------

        //----------------------------- App Settings -----------------------------------------------
        const val APP_LOCK_ENABLED = "lock_app"
        const val START_DATE_OF_MONTH = "start_date_of_month"
        const val SHOW_NOTIFICATIONS = "show_notifications"
        const val HIDE_CURRENT_BALANCE = "hide_current_balance"
        const val TRANSFERS_AS_INCOME_EXPENSE = "transfers_as_inc_exp"
        //----------------------------- App Settings -----------------------------------------------

        //-------------------------------- Customer Journey ----------------------------------------
        const val _CARD_DISMISSED = "_cj_dismissed"
        //-------------------------------- Customer Journey ----------------------------------------

        //----------------------------- Others -----------------------------------------------
        const val CATEGORY_SORT_ORDER = "categorySortOrder"
    }

    private val preferences = appContext.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun has(key: String): Boolean {
        return preferences.contains(key)
    }

    val all: Map<String, *>
        get() = preferences.all

    fun putInt(key: String, value: Int) {
        val editor = preferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun putFloat(key: String, value: Float) {
        val editor = preferences.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    fun putDouble(key: String, value: Double) {
        val editor = preferences.edit()
        editor.putFloat(key, value.toFloat())
        editor.apply()
    }

    fun putLong(key: String, value: Long) {
        val editor = preferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun putString(key: String, value: String?) {
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun <T> put(key: String, value: T?) {
        val editor = preferences.edit()
        editor.putString(key, gson.toJson(value))
        editor.apply()
    }

    fun getLong(key: String, defValue: Long): Long {
        return preferences.getLong(key, defValue)
    }

    fun getInt(key: String, defValue: Int): Int {
        return preferences.getInt(key, defValue)
    }

    fun getFloat(key: String, defValue: Float): Float {
        return preferences.getFloat(key, defValue)
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    fun getString(key: String): String {
        return preferences.getString(key, null)
            ?: throw IllegalStateException("SharePrefs key '$key' cannot be null")
    }

    fun getString(key: String, defValue: String?): String? {
        return preferences.getString(key, defValue)
    }

    fun putDate(key: String, date: LocalDateTime) {
        preferences.edit().putLong(key, date.toEpochSeconds()).apply()
    }

    fun getDate(key: String): LocalDateTime? {
        val timestamp = preferences.getLong(key, -1)
        return if (timestamp > 0) timestamp.epochSecondToDateTime() else null
    }

    fun getEpochSeconds(key: String): Long? {
        val epochSeconds = preferences.getLong(key, -1)
        return if (epochSeconds > 0) epochSeconds else null
    }

    operator fun <T> get(key: String, aClass: Class<T>): T? {
        val jsonString = preferences.getString(key, null)
        return gson.fromJson(jsonString, aClass)
    }

    fun remove(key: String) {
        preferences.edit().remove(key).apply()
    }

    fun removeAll() {
        preferences.edit().clear().apply()
    }
}