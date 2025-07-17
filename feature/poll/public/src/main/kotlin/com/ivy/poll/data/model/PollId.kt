package com.ivy.poll.data.model

enum class PollId {
  PaidIvy
}

@JvmInline
value class PollOption(val value: String)