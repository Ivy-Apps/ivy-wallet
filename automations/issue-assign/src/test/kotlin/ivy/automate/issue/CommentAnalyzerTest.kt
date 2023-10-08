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
            row("want to contribute", CommentIntention.TakeIssue(user)),
            row("I'm new to GitHub, can I work on this?", CommentIntention.TakeIssue(user)),
            row(
                "I would like to work on this.",
                CommentIntention.TakeIssue(user)
            ),
            row(
                "Hi,\nI would like to work on this issue",
                CommentIntention.TakeIssue(user)
            ),
            row(
                "I'm interested to contribute on this feature.",
                CommentIntention.TakeIssue(user)
            ),
            row(
                "Sure, let me give it a try. Will keep posted here.",
                CommentIntention.TakeIssue(user)
            ),
            row(
                "Hey @ILIYANGERMANOV , I want to work on this issue. Please assign it to me.\n",
                CommentIntention.TakeIssue(user)
            ),
            row(
                "Hi @ILIYANGERMANOV, Assign this issue to me." +
                        "I think i will require some help in it from your side, " +
                        "so please bear with me.\n",
                CommentIntention.TakeIssue(user)
            )
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