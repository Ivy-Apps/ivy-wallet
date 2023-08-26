package com.ivy.wallet.backup.github

data class GitHubCredentials(
    val owner: String,
    val repo: String,
    val accessToken: String,
)