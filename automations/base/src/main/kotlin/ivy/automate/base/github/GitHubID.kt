package ivy.automate.base.github

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import ivy.automate.base.Exact

@JvmInline
value class GitHubID private constructor(val value: String) {
    companion object : Exact<String, GitHubID> {
        override val exactName: String = "GitHubID"

        override fun Raise<String>.spec(raw: String): GitHubID {
            ensure(raw.isNotBlank()) { "Cannot be blank" }
            return GitHubID(raw.trim())
        }

    }
}