package ivy.automate.base.model

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import ivy.automate.base.github.model.GitHubIssueNumber
import org.junit.Test

class GitHubIssueNumberTest {

    @Test
    fun `invalid - blank string`() {
        GitHubIssueNumber.from("").shouldBeLeft()
    }

    @Test
    fun `invalid - not a number`() {
        GitHubIssueNumber.from("123a").shouldBeLeft()
    }

    @Test
    fun valid() {
        // given
        val id = "2763"

        // when
        val res = GitHubIssueNumber.from(id)

        // then
        res.shouldBeRight().value shouldBe "2763"
    }
}
