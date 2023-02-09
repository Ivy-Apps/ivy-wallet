package com.ivy.sync.calculation

import com.ivy.core.data.sync.SyncData
import com.ivy.core.data.sync.Syncable
import io.kotest.core.spec.style.FreeSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import java.time.LocalDateTime

class SyncDiffTest : FreeSpec({
    "test" {
        // TODO: WIP
        itemDiff(
            remote = SyncData(
                items = Arb.list(Arb.syncable()).next(),
                deleted = Arb.set(Arb.syncable()).next()
            ),
            local = SyncData(
                items = Arb.list(Arb.syncable()).next(),
                deleted = Arb.set(Arb.syncable()).next()
            ),
        )
    }
})

fun Arb.Companion.syncable(
    arbLastUpdated: Arb<LocalDateTime> = Arb.localDateTime(),
    arbRemoved: Arb<Boolean> = Arb.boolean(),
): Arb<Syncable> = arbitrary {
    val id = Arb.uuid().bind()
    val lastUpdated = arbLastUpdated.bind()
    val removed = arbRemoved.bind()

    object : Syncable {
        override val id = id
        override val lastUpdated = lastUpdated
        override val removed = removed
    }
}