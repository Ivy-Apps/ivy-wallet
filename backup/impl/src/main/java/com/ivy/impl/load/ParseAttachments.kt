package com.ivy.impl.load

import arrow.core.Either
import com.ivy.backup.base.ImportBackupError
import com.ivy.backup.base.maybe
import com.ivy.backup.base.parseItems
import com.ivy.common.time.provider.TimeProvider
import com.ivy.data.attachment.Attachment
import com.ivy.data.attachment.AttachmentSource
import com.ivy.data.attachment.AttachmentType
import org.json.JSONObject

internal fun parseAttachments(
    json: JSONObject,
    timeProvider: TimeProvider
): Either<ImportBackupError.Parse, List<Attachment>> =
    parseItems(
        json = json,
        key = "attachments",
        error = ImportBackupError.Parse::Attachments,
        parse = {
            Attachment(
                id = getString("id"),
                associatedId = getString("associatedId"),
                uri = getString("uri"),
                source = getInt("source").let(AttachmentSource::fromCode)
                    ?: error("Invalid attachment code - ${getInt("source")}!"),
                filename = maybe { getString("filename") },
                type = maybe { getInt("type").let(AttachmentType::fromCode) },
                sync = parseSync(timeProvider),
            )
        }
    )