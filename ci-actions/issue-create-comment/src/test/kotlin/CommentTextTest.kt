import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ivy.automate.base.Constants
import ivy.automate.base.github.model.GitHubIssue
import ivy.automate.base.github.model.GitHubIssueNumber
import ivy.automate.base.github.model.GitHubUser
import ivy.automate.base.github.model.GitHubUsername
import ivy.automate.issue.create.commentText

class CommentTextTest : FreeSpec({
    "the comment text should look good" {
        // given
        val issue = GitHubIssue(
            number = GitHubIssueNumber(value = "223"),
            creator = GitHubUser(GitHubUsername("user1")),
            assignee = null
        )

        // when
        val commentText = commentText(issue)

        // then
        commentText shouldBe """
            Thank you @user1 for raising Issue #223! 🚀
            What's next? Read our **[Contribution Guidelines](${Constants.CONTRIBUTING_URL}) 📚**.
            
            _Tagging @ILIYANGERMANOV for review & approval 👀_
        """.trimIndent()
    }
})