package com.ivy.base.legacy

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
@Deprecated("Use Tag Data Model")
@Suppress("DataClassTypedIDs")
data class LegacyTag(val id: UUID, val name: String)
