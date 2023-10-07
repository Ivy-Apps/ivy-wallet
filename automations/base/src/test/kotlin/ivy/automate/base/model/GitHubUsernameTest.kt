package ivy.automate.base.model

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ivy.automate.base.github.model.GitHubUsername

class GitHubUsernameTest : FreeSpec({
    "invalid username" {
        GitHubUsername.from("").shouldBeLeft()
        GitHubUsername.from(" ").shouldBeLeft()
        GitHubUsername.from("  ").shouldBeLeft()
    }

    "valid username" {
        // given
        val rawUsername = " ILIYANGERMANOV "

        // when
        val res = GitHubUsername.from(rawUsername)

        // then
        res.shouldBeRight().value shouldBe "ILIYANGERMANOV"
    }
})