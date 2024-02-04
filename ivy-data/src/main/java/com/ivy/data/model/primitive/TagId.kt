package com.ivy.data.model.primitive

import com.ivy.data.model.sync.UniqueId
import java.util.UUID

@JvmInline
value class TagId(override val value: UUID) : UniqueId