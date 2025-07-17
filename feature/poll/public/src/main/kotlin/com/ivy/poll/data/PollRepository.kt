package com.ivy.poll.data

import arrow.core.Either
import com.ivy.domain.usecase.android.DeviceId
import com.ivy.poll.data.model.PollId
import com.ivy.poll.data.model.PollOptionId

interface PollRepository {
  suspend fun hasVoted(poll: PollId): Boolean
  suspend fun setVoted(poll: PollId, voted: Boolean)
  suspend fun vote(
    deviceId: DeviceId,
    poll: PollId,
    option: PollOptionId
  ): Either<String, Unit>
}