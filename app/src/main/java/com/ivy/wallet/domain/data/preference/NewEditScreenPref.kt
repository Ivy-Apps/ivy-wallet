package com.ivy.wallet.domain.data.preference

import androidx.datastore.preferences.core.booleanPreferencesKey

data class NewEditScreenPref(
    override val value: Boolean = false
) : Preference<Boolean> {
    override val key = booleanPreferencesKey("exp_new_edit_screen")
}