package com.ivy.data.model.primitive

import androidx.compose.runtime.Immutable
import com.ivy.data.model.sync.UniqueId
import java.util.UUID

@JvmInline
@Immutable
value class TagId(override val value: UUID) : UniqueId