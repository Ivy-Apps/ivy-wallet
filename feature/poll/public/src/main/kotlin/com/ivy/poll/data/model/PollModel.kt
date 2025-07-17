package com.ivy.poll.data.model

enum class PollId(val id: String) {
  PaidIvy("paid-ivy"),
}

@Suppress("DataClassTypedIDs")
data class Poll(
  val id: PollId,
  val title: String,
  val description: String,
  val options: List<PollOption>
)

@Suppress("DataClassTypedIDs")
data class PollOption(
  val id: PollOptionId,
  val text: String,
)

@JvmInline
value class PollOptionId(val value: String)
