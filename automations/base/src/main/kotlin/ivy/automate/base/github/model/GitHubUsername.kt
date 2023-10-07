package ivy.automate.base.github.model

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import ivy.automate.base.Exact

@JvmInline
value class GitHubUsername private constructor(val value: String) {
    companion object : Exact<String, GitHubUsername> {
        override val exactName: String = "GitHubUsername"

        override fun Raise<String>.spec(raw: String): GitHubUsername {
            ensure(raw.isNotBlank()) { "Cannot be blank" }
            return GitHubUsername(raw.trim())
        }
    }
}