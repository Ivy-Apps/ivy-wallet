package ivy.automate.issue

import ivy.automate.base.github.model.GitHubComment
import ivy.automate.base.github.model.GitHubUser

val TakeIssueIntentionPhrases = listOf(
    "im on it",
    "i am on it",
)

sealed interface CommentIntention {
    data class TakeIssue(
        val user: GitHubUser
    ) : CommentIntention

    data object Unknown : CommentIntention
}

fun analyzeCommentIntention(comment: GitHubComment): CommentIntention {
    val commentText = comment.text.lowercase()
        .replace("'", "")

    return when {
        TakeIssueIntentionPhrases.any { it in commentText }
        -> CommentIntention.TakeIssue(comment.author)

        else -> CommentIntention.Unknown
    }
}