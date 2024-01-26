package ivy.automate.base.github.model

data class GitHubIssue(
    // TODO: Add support for GitHub issue number here
    val creator: GitHubUser,
    val assignee: GitHubUser?
)
