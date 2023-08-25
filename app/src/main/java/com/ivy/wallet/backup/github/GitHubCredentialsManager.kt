package com.ivy.wallet.backup.github

import arrow.core.Either
import javax.inject.Inject

class GitHubCredentialsManager @Inject constructor(

) {
    suspend fun getCredentials(): Either<String, GitHubCredentials> {
        return Either.Right(
            GitHubCredentials(
                owner = "ILIYANGERMANOV",
                repo = "ivy-wallet-backup-test",
                accessToken = "",
            )
        )
    }
}

