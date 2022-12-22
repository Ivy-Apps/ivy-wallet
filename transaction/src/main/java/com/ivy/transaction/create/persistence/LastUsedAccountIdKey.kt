package com.ivy.transaction.create.persistence

import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject

class LastUsedAccountIdKey @Inject constructor() {
    val key by lazy { stringPreferencesKey("last_used_account_id") }
}