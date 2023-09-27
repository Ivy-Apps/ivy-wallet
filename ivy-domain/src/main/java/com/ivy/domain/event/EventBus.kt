package com.ivy.domain.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventBus @Inject constructor() {
    private val eventFlow = MutableSharedFlow<Event>(
        replay = 0,
        extraBufferCapacity = 1
    )

    sealed interface Event

    /**
     * @param events the events that you want to be notified about
     * or empty to listen for all
     */
    suspend fun subscribe(
        vararg events: Event,
        onEvent: suspend (Event) -> Unit
    ) {
        eventFlow.filter { events.isEmpty() || it in events }
            .collectLatest {
                onEvent(it)
            }
    }

    fun post(event: Event) {
        eventFlow.tryEmit(event)
    }
}