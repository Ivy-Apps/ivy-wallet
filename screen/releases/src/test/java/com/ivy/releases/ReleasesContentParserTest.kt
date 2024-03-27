package com.ivy.releases

import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import org.junit.Before
import org.junit.Test

class ReleasesContentParserTest {

    private lateinit var releasesContentParser: ReleasesContentParser

    @Before
    fun setup() {
        releasesContentParser = ReleasesContentParser()
    }

    @Test
    fun `toCommitsList - with null commits input`() {
        releasesContentParser.toCommitsList(null) shouldBe persistentListOf()
    }

    @Test
    fun `toCommitsList - with blank commits input`() {
        releasesContentParser.toCommitsList("") shouldBe persistentListOf()
    }

    @Test
    fun `toCommitsList - with 4 commits input`() {
        // given
        val commits = "- c9a985eb Add `GitHubWorkerMigration` and bump version to \"4.4.2\" " +
                "(142)\n - e6e001e8 Update dev-request.yml\n - 2d397ca1 Update bug_report.yml\n " +
                "- 2c386031 Update and rename dev-contributor-request.yml to dev-request.yml"

        // when
        val res = releasesContentParser.toCommitsList(commits)

        // then
        res shouldBe persistentListOf(
            "c9a985eb Add `GitHubWorkerMigration` and bump version to \"4.4.2\" (142)",
            "e6e001e8 Update dev-request.yml",
            "2d397ca1 Update bug_report.yml",
            "2c386031 Update and rename dev-contributor-request.yml to dev-request.yml"
        )
    }

    @Test
    fun `toReleaseDate - formats the date`() {
        // given
        val date = "2023-09-16T17:42:08Z"

        // when
        val dateFormatted = releasesContentParser.toReleaseDate(date)

        // then
        dateFormatted shouldBe "2023-09-16"
    }
}
