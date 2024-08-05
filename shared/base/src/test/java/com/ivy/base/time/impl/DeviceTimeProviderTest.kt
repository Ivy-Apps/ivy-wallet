package com.ivy.base.time.impl

import org.junit.Before
import org.junit.Test

class DeviceTimeProviderTest {

    private lateinit var timeProvider: DeviceTimeProvider

    @Before
    fun setup() {
        timeProvider = DeviceTimeProvider()
    }

    @Test
    fun `validate no crashes`() {
        // When
        timeProvider.getZoneId()
        timeProvider.utcNow()
        timeProvider.localNow()

        // Then
        // there are no crashes
    }
}