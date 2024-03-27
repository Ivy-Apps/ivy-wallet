package ivy.automate.base.model

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import ivy.automate.base.github.model.GitHubPAT
import org.junit.Test

class GitHubPATTest {
    @Test
    fun `invalid - blank string`() {
        GitHubPAT.from("").shouldBeLeft()
        GitHubPAT.from(" ").shouldBeLeft()
        GitHubPAT.from("  ").shouldBeLeft()
    }

    @Test
    fun valid() {
        // given
        val token = " abc "

        // when
        val res = GitHubPAT.from(token)

        // then
        res.shouldBeRight().value shouldBe "abc"
    }
}
