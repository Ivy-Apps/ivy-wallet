package com.ivy.poll.impl.data

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import arrow.core.Either
import com.ivy.data.datastore.IvyDataStore
import com.ivy.poll.data.PollRepository
import com.ivy.poll.data.model.PollId
import com.ivy.poll.data.model.PollOptionId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PollRepositoryImpl @Inject constructor(
  private val dataStore: IvyDataStore
) : PollRepository {
  override suspend fun hasVoted(poll: PollId): Boolean {
    return dataStore.data.map { it[votedKey(poll)] ?: false }.first()
  }

  override suspend fun vote(poll: PollId, option: PollOptionId): Either<String, Unit> {
    TODO("Not yet implemented")
  }

  private fun votedKey(poll: PollId): Preferences.Key<Boolean> {
    return booleanPreferencesKey("poll.${poll.id}_voted")
  }
}