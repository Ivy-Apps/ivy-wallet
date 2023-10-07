package ivy.automate.base.github

import arrow.core.Either
import io.ktor.client.call.body
import io.ktor.client.request.get
import ivy.automate.base.catchIO
import ivy.automate.base.ktor.KtorClientScope
import kotlinx.serialization.Serializable

class GitHubServiceImpl : GitHubService {
    companion object {
        private const val BASE_URL = "https://api.github.com/repos/Ivy-Apps/ivy-wallet"
        private const val API_ISSUES = "$BASE_URL/issues"
    }

    context(KtorClientScope)
    override suspend fun fetchIssue(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, IssueDto> = catchIO {
        ktorClient.get("$API_ISSUES/${issueNumber.value}").body()
    }

    context(KtorClientScope)
    override suspend fun fetchLabels(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, List<LabelDto>> = catchIO {
        ktorClient.get("$API_ISSUES/${issueNumber.value}/labels").body()
    }

    context(KtorClientScope)
    override suspend fun fetchComments(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, List<CommentDto>> = catchIO {
        ktorClient.get("$API_ISSUES/${issueNumber.value}/comments").body()
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