package com.ivy.wallet.domain.data.preference

import androidx.datastore.preferences.core.Preferences

interface Preference<T> {
    val key: Preferences.Key<T>
    val value: T?
}