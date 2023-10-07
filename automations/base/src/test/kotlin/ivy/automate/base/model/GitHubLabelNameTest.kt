package ivy.automate.base.model

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ivy.automate.base.github.model.GitHubLabelName

class GitHubLabelNameTest : FreeSpec({
    "invalid username" {
        GitHubLabelName.from("").shouldBeLeft()
        GitHubLabelName.from(" ").shouldBeLeft()
        GitHubLabelName.from("  ").shouldBeLeft()
    }

    "valid username" {
        // given
        val rawLabel = " devexp "

        // when
        val res = GitHubLabelName.from(rawLabel)

        // then
        res.shouldBeRight().value shouldBe "devexp"
    }
})