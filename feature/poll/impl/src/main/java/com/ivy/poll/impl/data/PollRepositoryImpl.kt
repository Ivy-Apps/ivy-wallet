package com.ivy.poll.impl.data

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import arrow.core.Either
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ivy.data.datastore.IvyDataStore
import com.ivy.domain.usecase.android.DeviceId
import com.ivy.poll.data.PollRepository
import com.ivy.poll.data.model.PollId
import com.ivy.poll.data.model.PollOptionId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PollRepositoryImpl @Inject constructor(
  private val dataStore: IvyDataStore
) : PollRepository {
  override suspend fun hasVoted(poll: PollId): Boolean {
    return dataStore.data.map { it[votedKey(poll)] ?: false }.first()
  }

  override suspend fun setVoted(poll: PollId, voted: Boolean) {
    dataStore.edit {
      it[votedKey(poll)] = voted
    }
  }

  override suspend fun vote(
    deviceId: DeviceId,
    poll: PollId,
    option: PollOptionId
  ): Either<String, Unit> = suspendCoroutine { cont ->
    Firebase.firestore
      .collection("polls/${poll.id}")
      .add(mapOf(deviceId.value to option.value))
      .addOnSuccessListener {
        cont.resume(Either.Right(Unit))
      }
      .addOnFailureListener { e ->
        val message = e.message ?: "null message"
        cont.resume(Either.Left("FireStore: $message"))
      }
  }

  private fun votedKey(poll: PollId): Preferences.Key<Boolean> {
    return booleanPreferencesKey("poll.${poll.id}_voted")
  }
}