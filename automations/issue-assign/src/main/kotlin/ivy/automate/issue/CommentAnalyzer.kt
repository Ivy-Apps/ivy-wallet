package ivy.automate.issue

import ivy.automate.base.github.model.GitHubComment
import ivy.automate.base.github.model.GitHubUser

sealed interface CommentIntention {
    data class TakeIssue(
        val user: GitHubUser
    ) : CommentIntention

    data object Unknown : CommentIntention
}

fun analyzeCommentIntention(comment: GitHubComment): CommentIntention {
    val commentText = comment.text.lowercase()
        .replace("'", "")

    fun keyphrase(phrase: String): Boolean {
        return phrase in commentText
    }

    return when {
        keyphrase("im on it")
        -> CommentIntention.TakeIssue(comment.author)

        else -> CommentIntention.Unknown
    }
}