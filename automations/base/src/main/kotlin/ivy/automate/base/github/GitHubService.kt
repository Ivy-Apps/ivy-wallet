package ivy.automate.base.github

import arrow.core.Either
import ivy.automate.base.ktor.KtorClientScope

interface GitHubService {
    context(KtorClientScope)
    suspend fun fetchIssue(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, IssueDto>


    context(KtorClientScope)
    suspend fun fetchLabels(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, List<LabelDto>>

    context(KtorClientScope)
    suspend fun fetchComments(
        issueNumber: GitHubIssueNumber
    ): Either<Throwable, List<CommentDto>>
}