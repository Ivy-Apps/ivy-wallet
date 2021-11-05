package com.ivy.wallet.network.service

import com.ivy.wallet.network.request.github.OpenIssueRequest
import com.ivy.wallet.network.request.github.OpenIssueResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GithubService {
    companion object {
        private const val BASE_URL = "https://api.github.com"
        const val OPEN_ISSUE_URL = "$BASE_URL/repos/ILIYANGERMANOV/ivy-wallet/issues"

        const val LABEL_USER_REQUEST = "user request"
    }

    @POST(OPEN_ISSUE_URL)
    suspend fun openIssue(
        @Body request: OpenIssueRequest
    ): OpenIssueResponse
}