package com.ivy.releases

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf

class ReleasesContentParserTest : FreeSpec({
    val releasesContentParser = ReleasesContentParser()

    "toCommitsList" - {
        val commits = "- c9a985eb Add `GitHubWorkerMigration` and bump version to \"4.4.2\" " +
                "(142)\n - e6e001e8 Update dev-request.yml\n - 2d397ca1 Update bug_report.yml\n " +
                "- 2c386031 Update and rename dev-contributor-request.yml to dev-request.yml"

        "with null commits input" {
            releasesContentParser.toCommitsList(null) shouldBe persistentListOf()
        }

        "with blank commits input" {
            releasesContentParser.toCommitsList("") shouldBe persistentListOf()
        }

        "with 4 commits input" {
            releasesContentParser.toCommitsList(commits) shouldBe persistentListOf(
                "c9a985eb Add `GitHubWorkerMigration` and bump version to \"4.4.2\" (142)",
                "e6e001e8 Update dev-request.yml",
                "2d397ca1 Update bug_report.yml",
                "2c386031 Update and rename dev-contributor-request.yml to dev-request.yml"
            )
        }
    }

    "toReleaseDate" - {
        val date = "2023-09-16T17:42:08Z"

        "should return 2023-09-16 from \"2023-09-16T17:42:08Z\"" {
            releasesContentParser.toReleaseDate(date) shouldBe "2023-09-16"
        }
    }
})