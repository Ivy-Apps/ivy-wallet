package com.ivy.wallet.io.network.service

import com.ivy.wallet.io.network.request.github.OpenIssueRequest
import com.ivy.wallet.io.network.request.github.OpenIssueResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GithubService {
    companion object {
        const val BASE_URL = "https://api.github.com"
        const val OPEN_ISSUE_URL = "$BASE_URL/repos/ILIYANGERMANOV/ivy-wallet/issues"

        const val GITHUB_SERVICE_ACC_USERNAME = "ivywallet"

        //Split Github Access token in two parts so Github doesn't delete it
        //because "Personal access token was found in commit."
        const val GITHUB_SERVICE_ACC_ACCESS_TOKEN_PART_1 = "ghp_MuvrbtIH897"
        const val GITHUB_SERVICE_ACC_ACCESS_TOKEN_PART_2 = "JASL6i8mBvXJ3aM7DLk4U9Gwq"
    }

    @POST(OPEN_ISSUE_URL)
    suspend fun openIssue(
        @Header("Accept") accept: String = "application/vnd.github.v3+json",
        @Body request: OpenIssueRequest
    ): OpenIssueResponse
}