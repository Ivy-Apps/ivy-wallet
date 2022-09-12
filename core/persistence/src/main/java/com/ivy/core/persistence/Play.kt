package com.ivy.core.persistence

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Item() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.create()
    var isComplete: Boolean = false
    var summary: String = ""
    var owner_id: String = ""

    constructor(ownerId: String = "") : this() {
        owner_id = ownerId
    }
}

suspend fun test() {
    val item = Item()

    val cfg = RealmConfiguration.Builder(schema = setOf(Item::class))
        .build()

    val db = Realm.open(cfg)

    db.write {
        copyToRealm(item.apply {
            isComplete = true
        })
    }
}