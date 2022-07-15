package com.ivy.wallet.domain.action.settings.preference

import com.ivy.frp.action.FPAction
import com.ivy.frp.asParamTo
import com.ivy.temp.persistence.Preference
import com.ivy.wallet.io.persistence.datastore.IvyDataStore
import javax.inject.Inject

class SetPreferenceAct<P : Preference<V>, V> @Inject constructor(
    private val dataStore: IvyDataStore
) : FPAction<P, Unit>() {
    override suspend fun P.compose(): suspend () -> Unit =
        when (val newValue = value) {
            newValue != null -> (key to newValue) asParamTo dataStore::insert
            else -> key asParamTo dataStore::remove
        }

}