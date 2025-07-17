package com.ivy.poll.data

import arrow.core.Either
import com.ivy.poll.data.model.PollId

interface PollRepository {
  suspend fun hasVoted(poll: PollId): Boolean
  suspend fun vote(poll: PollId, option: String): Either<String, Unit>
}