package com.ivy.old.parse

import arrow.core.Either
import com.ivy.backup.base.ImportBackupError
import com.ivy.backup.base.data.SettingsData
import com.ivy.backup.base.optional
import com.ivy.data.Theme
import org.json.JSONObject

fun parseSettings(
    json: JSONObject
): Either<ImportBackupError, SettingsData> = Either.catch(
    ImportBackupError.Parse::Settings
) {
    val settingsJson = json.getJSONArray("settings")
        .getJSONObject(0)

    SettingsData(
        baseCurrency = settingsJson.getString("currency"),
        theme = when (optional { settingsJson.get("theme") }) {
            "DARK" -> Theme.Dark
            "LIGHT" -> Theme.Light
            else -> Theme.Auto
        }
    )
}