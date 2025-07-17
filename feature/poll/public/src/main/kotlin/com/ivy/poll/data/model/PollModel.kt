package com.ivy.poll.data.model

enum class PollId(val id: String) {
  PaidIvy("paid-ivy"),
}

data class Poll(
  val id: PollId,
  val title: String,
  val options: List<PollOption>
)

data class PollOption(
  val id: PollOptionId,
  val text: String,
)

@JvmInline
value class PollOptionId(val value: String)
