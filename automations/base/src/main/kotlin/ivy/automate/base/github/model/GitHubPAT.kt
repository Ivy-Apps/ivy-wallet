package ivy.automate.base.github.model

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import ivy.automate.base.Exact

@JvmInline
value class GitHubPAT private constructor(val value: String) {
    companion object : Exact<String, GitHubPAT> {
        override val exactName: String = "GitHubPAT"

        override fun Raise<String>.spec(raw: String): GitHubPAT {
            ensure(raw.isNotBlank()) { "Cannot be blank" }
            return GitHubPAT(raw.trim())
        }
    }
}