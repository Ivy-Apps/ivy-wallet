package ivy.automate.issue

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import io.kotest.matchers.shouldBe
import ivy.automate.base.github.model.GitHubComment
import ivy.automate.base.github.model.GitHubUser
import ivy.automate.base.github.model.GitHubUsername
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class CommentAnalyzerTest {

    enum class DetermineIntentionTestCase(
        val text: String,
        val expectedIntention: CommentIntention,
    ) {
        EmptyText("", CommentIntention.Unknown),
        BlankText(" ", CommentIntention.Unknown),
        Q1("Is this implemented?", CommentIntention.Unknown),
        ImOnIt1("I'm on it", CommentIntention.TakeIssue(User)),
        ImOnIt2("Im on it", CommentIntention.TakeIssue(User)),
        ImOnIt3("im on it", CommentIntention.TakeIssue(User)),
        ImOnIt4("I am on it", CommentIntention.TakeIssue(User)),
        ImOnItInSentence(
            "yeah i am on it, also i wanted to ask " +
                    "what resources should i use learn android",
            CommentIntention.TakeIssue(User)
        ),
    }

    @Test
    fun `determines text intention`(
        @TestParameter testCase: DetermineIntentionTestCase
    ) = runTest {
        // given
        val comment = newComment(testCase.text)

        // when
        val intention = analyzeCommentIntention(comment)

        // then
        intention shouldBe testCase.expectedIntention
    }

    private fun newComment(text: String) = GitHubComment(
        author = User,
        text = text,
    )

    companion object {
        val User = GitHubUser(
            username = GitHubUsername("username")
        )
    }
}
