package ivy.automate.issue

import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import ivy.automate.base.github.model.GitHubComment
import ivy.automate.base.github.model.GitHubUser
import ivy.automate.base.github.model.GitHubUsername

class CommentAnalyzerTest : FreeSpec({
    val user = GitHubUser(
        username = GitHubUsername("username")
    )

    fun newComment(text: String) = GitHubComment(
        author = user,
        text = text,
    )

    "determines text intention" - {
        withData(
            nameFn = { (text, intention) ->
                val name = when (intention) {
                    is CommentIntention.TakeIssue -> "TakeIssue"
                    CommentIntention.Unknown -> "Unknown"
                }
                "\"$text\" is $name"
            },
            row("", CommentIntention.Unknown),
            row(" ", CommentIntention.Unknown),
            row("Is this implemented?", CommentIntention.Unknown),
            row("I'm on it", CommentIntention.TakeIssue(user)),
            row("Im on it", CommentIntention.TakeIssue(user)),
            row("im on it", CommentIntention.TakeIssue(user)),
        ) { (text, expectedIntention) ->
            // given
            val comment = newComment(text)

            // when
            val intention = analyzeCommentIntention(comment)

            // then
            intention shouldBe expectedIntention
        }

    }
})