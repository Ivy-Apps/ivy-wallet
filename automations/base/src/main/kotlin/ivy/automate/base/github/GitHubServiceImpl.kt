package ivy.automate.base.github

import arrow.core.Either
import arrow.core.raise.either
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import ivy.automate.base.IvyDsl
import ivy.automate.base.catchIO
import ivy.automate.base.getOrThrow
import ivy.automate.base.github.model.GitHubComment
import ivy.automate.base.github.model.GitHubIssue
import ivy.automate.base.github.model.GitHubIssueNumber
import ivy.automate.base.github.model.GitHubLabel
import ivy.automate.base.github.model.GitHubLabelName
import ivy.automate.base.github.model.GitHubPAT
import ivy.automate.base.github.model.GitHubUser
import ivy.automate.base.github.model.GitHubUsername
import ivy.automate.base.github.model.NotBlankTrimmedString
import kotlinx.serialization.Serializable

class GitHubServiceImpl(
    private val ktorClient: HttpClient,
) : GitHubService {
    companion object {
        private const val BASE_URL = "https://api.github.com/repos/Ivy-Apps/ivy-wallet"
    }

    override suspend fun fetchIssue(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, GitHubIssue> = catchIO {
        val issueDto = ktorClient.get(issueUrl(issueNumber)).body<IssueDto>()
        GitHubIssue(
            assignee = issueDto.assignee?.toDomain()?.getOrThrow()
        )
    }

    override suspend fun fetchIssueLabels(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, List<GitHubLabel>> = catchIO {
        val labelsDto = ktorClient.get("${issueUrl(issueNumber)}/labels")
            .body<List<LabelDto>>()
        labelsDto.map {
            GitHubLabel(
                name = GitHubLabelName(it.name)
            )
        }
    }

    override suspend fun fetchIssueComments(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, List<GitHubComment>> = catchIO {
        val commentsDto = ktorClient.get(commentsUrl(issueNumber))
            .body<List<CommentDto>>()
        commentsDto.map {
            GitHubComment(
                author = it.user.toDomain().getOrThrow(),
                text = it.body,
            )
        }
    }

    override suspend fun commentIssue(
        pat: GitHubPAT,
        issueNumber: GitHubIssueNumber,
        text: NotBlankTrimmedString
    ): Either<Throwable, Unit> = catchIO {
        val response = ktorClient.post(commentsUrl(issueNumber)) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${pat.value}")
            setBody(CommentPayload(text.value))
        }
        response.requireSuccess()
    }

    override suspend fun assignIssue(
        pat: GitHubPAT,
        issueNumber: GitHubIssueNumber,
        assignee: GitHubUsername
    ): Either<Throwable, Unit> = catchIO {
        val response = ktorClient.post("${issueUrl(issueNumber)}/assignees") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${pat.value}")
            setBody(AssigneesPayload(listOf(assignee.value)))
        }
        response.requireSuccess()
    }

    private fun commentsUrl(issueNumber: GitHubIssueNumber): String {
        return "${issueUrl(issueNumber)}/comments"
    }

    private fun issueUrl(issueNumber: GitHubIssueNumber): String {
        return "$BASE_URL/issues/${issueNumber.value}"
    }

    private fun UserDto.toDomain(): Either<String, GitHubUser> = either {
        GitHubUser(
            username = GitHubUsername.from(login).bind()
        )
    }

    @IvyDsl
    private fun HttpResponse.requireSuccess() {
        require(status.isSuccess()) { status.toString() }
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
}