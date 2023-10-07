package ivy.automate.base.github

import arrow.core.Either
import ivy.automate.base.github.model.GitHubComment
import ivy.automate.base.github.model.GitHubIssue
import ivy.automate.base.github.model.GitHubIssueNumber
import ivy.automate.base.github.model.GitHubLabel
import ivy.automate.base.github.model.GitHubPAT
import ivy.automate.base.github.model.GitHubUsername
import ivy.automate.base.github.model.NotBlankTrimmedString
import ivy.automate.base.ktor.KtorClientScope

interface GitHubService {
    context(KtorClientScope)
    suspend fun fetchIssue(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, GitHubIssue>

    context(KtorClientScope)
    suspend fun fetchIssueLabels(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, List<GitHubLabel>>

    context(KtorClientScope)
    suspend fun fetchIssueComments(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, List<GitHubComment>>

    context(KtorClientScope)
    suspend fun commentIssue(
        pat: GitHubPAT,
        issueNumber: GitHubIssueNumber,
        text: NotBlankTrimmedString
    ): Either<Throwable, Unit>

    context(KtorClientScope)
    suspend fun assignIssue(
        pat: GitHubPAT,
        issueNumber: GitHubIssueNumber,
        assignee: GitHubUsername
    ): Either<Throwable, Unit>
}