package ivy.automate.base.github.model

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import ivy.automate.base.Exact

@JvmInline
value class GitHubLabelName private constructor(val value: String) {
    companion object : Exact<String, GitHubLabelName> {
        override val exactName: String = "GitHubLabelName"

        override fun Raise<String>.spec(raw: String): GitHubLabelName {
            ensure(raw.isNotBlank()) { "Cannot be blank" }
            return GitHubLabelName(raw.trim())
        }
    }
}