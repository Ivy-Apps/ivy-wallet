package com.ivy.impl.export.json

import com.ivy.core.persistence.dao.AttachmentDao
import org.json.JSONArray

internal suspend fun exportAttachmentsJson(
    attachmentDao: AttachmentDao
): JSONArray = exportJson(
    findAll = attachmentDao::findAllBlocking,
    json = {
        put("id", it.id)
        put("associatedId", it.associatedId)
        put("uri", it.uri)
        put("source", it.source.code)
        put("filename", it.filename)
        put("type", it.type?.code)
        putSync(it.sync, it.lastUpdated)
    }
)