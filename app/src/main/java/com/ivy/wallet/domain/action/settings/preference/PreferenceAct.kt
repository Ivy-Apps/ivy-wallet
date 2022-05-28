package com.ivy.wallet.domain.action.settings.preference

import com.ivy.frp.action.FPAction
import com.ivy.frp.asParamTo
import com.ivy.wallet.domain.data.preference.Preference
import com.ivy.wallet.io.persistence.datastore.IvyDataStore
import javax.inject.Inject

class PreferenceAct<P : Preference<V>, V> @Inject constructor(
    private val dataStore: IvyDataStore
) : FPAction<P, V?>() {
    override suspend fun P.compose(): suspend () -> V? =
        this.key asParamTo dataStore::get
}