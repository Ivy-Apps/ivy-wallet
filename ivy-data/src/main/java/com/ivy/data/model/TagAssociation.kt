package com.ivy.data.model

import com.ivy.data.model.primitive.AssociationId
import com.ivy.data.model.primitive.TagId
import com.ivy.data.model.sync.Syncable
import java.time.Instant

data class TagAssociation(
    override val id: TagId,
    val associatedId: AssociationId,
    override val lastUpdated: Instant,
    override val removed: Boolean,
) : Syncable