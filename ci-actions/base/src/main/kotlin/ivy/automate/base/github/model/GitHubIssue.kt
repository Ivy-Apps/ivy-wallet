package ivy.automate.base.github.model

data class GitHubIssue(
    val creator: GitHubUser,
    val assignee: GitHubUser?
)
