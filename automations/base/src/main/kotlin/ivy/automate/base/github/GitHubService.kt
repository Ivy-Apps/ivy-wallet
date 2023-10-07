package ivy.automate.base.github

import arrow.core.Either
import ivy.automate.base.ktor.KtorClientScope

interface GitHubService {
    context(KtorClientScope)
    suspend fun fetchIssue(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, IssueDto>

    context(KtorClientScope)
    suspend fun fetchIssueLabels(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, List<LabelDto>>

    context(KtorClientScope)
    suspend fun fetchIssueComments(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, List<CommentDto>>

    context(KtorClientScope)
    suspend fun commentIssue(
        pat: GitHubPAT,
        issueNumber: GitHubIssueNumber,
        text: String
    ): Either<Throwable, Unit>

    context(KtorClientScope)
    suspend fun assignIssue(
        pat: GitHubPAT,
        issueNumber: GitHubIssueNumber,
        assignee: String
    ): Either<Throwable, Unit>
}