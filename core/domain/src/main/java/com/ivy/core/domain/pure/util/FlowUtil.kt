package com.ivy.core.domain.pure.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * @return list of flows -> flow of list
 */
inline fun <reified T> combineList(flows: List<Flow<T>>): Flow<List<T>> =
    if (flows.isEmpty()) flowOf(emptyList()) else combine(flows, Array<T>::toList)

inline fun <reified T, reified R> combineSafe(
    flows: List<Flow<T>>,
    ifEmpty: R,
    crossinline transform: suspend (List<T>) -> R,
): Flow<R> = if (flows.isEmpty()) flowOf(ifEmpty) else
    combine(flows) { res -> transform(res.toList()) }

@OptIn(ExperimentalCoroutinesApi::class)
inline fun <reified T> Flow<Flow<T>>.flattenLatest(): Flow<T> =
    flatMapLatest { it }