package ivy.automate.base.github

import arrow.core.Either
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import ivy.automate.base.catchIO
import ivy.automate.base.ktor.KtorClientScope
import kotlinx.serialization.Serializable

class GitHubServiceImpl : GitHubService {
    companion object {
        private const val BASE_URL = "https://api.github.com/repos/Ivy-Apps/ivy-wallet"
    }

    context(KtorClientScope)
    override suspend fun fetchIssue(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, IssueDto> = catchIO {
        ktorClient.get(issueUrl(issueNumber)).body()
    }

    context(KtorClientScope)
    override suspend fun fetchIssueLabels(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, List<LabelDto>> = catchIO {
        ktorClient.get("${issueUrl(issueNumber)}/labels").body()
    }

    context(KtorClientScope)
    override suspend fun fetchIssueComments(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, List<CommentDto>> = catchIO {
        ktorClient.get(commentsUrl(issueNumber)).body()
    }

    context(KtorClientScope)
    override suspend fun commentIssue(
        pat: GitHubPAT,
        issueNumber: GitHubIssueNumber,
        text: String
    ): Either<Throwable, Unit> = catchIO {
        val response = ktorClient.post(commentsUrl(issueNumber)) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${pat.value}")
            setBody(CommentPayload(text))
        }
        require(response.status.isSuccess()) {
            response.status.toString()
        }
    }

    context(KtorClientScope)
    override suspend fun assignIssue(
        pat: GitHubPAT,
        issueNumber: GitHubIssueNumber,
        assignee: String
    ): Either<Throwable, Unit> = catchIO {
        val response = ktorClient.put("${issueUrl(issueNumber)}/assignees") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${pat.value}")
            setBody(AssigneesPayload(listOf(assignee)))
        }
        require(response.status.isSuccess()) {
            response.status.toString()
        }
    }

    private fun commentsUrl(issueNumber: GitHubIssueNumber): String {
        return "${issueUrl(issueNumber)}/comments"
    }

    private fun issueUrl(issueNumber: GitHubIssueNumber): String {
        return "$BASE_URL/issues/${issueNumber.value}"
    }
}

@Serializable
data class IssueDto(
    val assignee: UserDto?
)

@Serializable
data class LabelDto(
    val name: String,
)

@Serializable
data class CommentDto(
    val body: String,
    val user: UserDto,
)

@Serializable
data class UserDto(
    val login: String,
)

@Serializable
data class CommentPayload(val body: String)

@Serializable
data class AssigneesPayload(val assignees: List<String>)
