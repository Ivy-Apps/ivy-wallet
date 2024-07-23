package ivy.automate.pr

import arrow.core.Either

class ClosesIssueAnalyzer : PRDescriptionAnalyzer {
    override fun analyze(prDescription: String): Either<String, Unit> {
        val cleanedDescription = prDescription.removeMarkdownComments()

        // Regex pattern to find "Closes #NUMBER" or "Closes N/A"
        val closesPattern = Regex("""(?i)closes\s+#\d+|closes\s+n/a""")
        if (closesPattern.containsMatchIn(cleanedDescription)) {
            return Either.Right(Unit)
        }

        return Either.Left(MissingClosesProblem)
    }

    private fun String.removeMarkdownComments(): String =
        replace(Regex("<!--.*?-->", RegexOption.DOT_MATCHES_ALL), "").trim()

    companion object {
        val MissingClosesProblem = buildString {
            append("[PROBLEM] Missing Closes GitHub Issue section\n")
            append("This PR does not close any GitHub issues. ")
            append("Add \"Closes #123\" where 123 is the issue number ")
            append("or \"Closes N/A\" if doesn't close any issue.")
        }
    }
}