package ivy.automate.issue

import ivy.automate.base.github.model.GitHubComment
import ivy.automate.base.github.model.GitHubUser

val TakeIssueIntentionPhrases = listOf(
    "im on it",
    "want to contribute",
    "work on this",
    "contribute on this",
    "assign to me",
    "assign this issue to me",
    "assign this to me",
    "give it a try",
    "i take this"
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