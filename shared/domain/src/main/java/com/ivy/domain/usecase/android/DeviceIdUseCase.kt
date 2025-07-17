package com.ivy.domain.usecase.android

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ivy.data.datastore.IvyDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class DeviceIdUseCase @Inject constructor(
  private val dataStore: IvyDataStore,
) {
  /**
   * Currently a dummy impl that uses a UUID stored in the data store.
   *
   * @return a unique identifier for the device without requiring permissions
   */
  suspend fun getDeviceId(): DeviceId {
    val currentId = dataStore.data.map { it[DEVICE_ID_KEY] }.first()
    if (currentId != null) return DeviceId(currentId)
    val newId = generateNewDeviceId()
    dataStore.edit { it[DEVICE_ID_KEY] = newId.value }
    return newId
  }

  private suspend fun generateNewDeviceId(): DeviceId {
    return DeviceId(UUID.randomUUID().toString())
  }

  companion object {
    private val DEVICE_ID_KEY = stringPreferencesKey("device_id")
  }
}

@JvmInline
value class DeviceId(val value: String)