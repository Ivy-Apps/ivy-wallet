package ivy.automate.base.github.model

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import ivy.automate.base.Exact

@JvmInline
value class GitHubIssueNumber private constructor(val value: String) {
    companion object : Exact<String, GitHubIssueNumber> {
        override val exactName: String = "GitHubIssueNumber"

        override fun Raise<String>.spec(raw: String): GitHubIssueNumber {
            ensure(raw.isNotBlank()) { "Cannot be blank" }
            ensure(raw.trim().all { it.isDigit() }) { "Must contain only digits" }
            return GitHubIssueNumber(raw.trim())
        }
    }
}