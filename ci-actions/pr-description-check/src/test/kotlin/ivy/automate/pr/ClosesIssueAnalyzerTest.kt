package ivy.automate.pr

import arrow.core.Either
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class ClosesIssueAnalyzerTest {

    private lateinit var analyzer: PRDescriptionAnalyzer

    @Before
    fun setup() {
        analyzer = ClosesIssueAnalyzer()
    }

    enum class PRDescTestCase(
        val prDescription: String,
        val expectedResult: Either<String, Unit>
    ) {
        VALID_SHORT_1(
            prDescription = """
                ## Pull request (PR) checklist
                Please check if your pull request fulfills the following requirements:
                <!--💡 Tip: Tick checkboxes like this: [x] 💡-->
                - [x] I've read the [Contribution Guidelines](https://github.com/Ivy-Apps/ivy-wallet/blob/main/CONTRIBUTING.md) and my PR doesn't break the rules.
                - [x] I've read and understand the [Developer Guidelines](https://github.com/Ivy-Apps/ivy-wallet/blob/main/docs/Guidelines.md).
                - [x] I confirm that I've run the code locally and everything works as expected.
                - [x] My PR includes only the necessary changes to fix the issue (i.e., no unnecessary files or lines of code are changed).
                - [x] 🎬 I've attached a **screen recording** of using the new code to the next paragraph (if applicable).

                ## What's changed?
                 - The new name reflects the full-featured nature of the application 
                ## Does this PR close any GitHub issues?
                - Closes #3361 
            """.trimIndent(),
            expectedResult = Either.Right(Unit),
        ),
        INVALID_SHORT_1(
            prDescription = """
                ## Pull request (PR) checklist
                Please check if your pull request fulfills the following requirements:
                <!--💡 Tip: Tick checkboxes like this: [x] 💡-->
                - [x] I've read the [Contribution Guidelines](https://github.com/Ivy-Apps/ivy-wallet/blob/main/CONTRIBUTING.md) and my PR doesn't break the rules.
                - [x] I've read and understand the [Developer Guidelines](https://github.com/Ivy-Apps/ivy-wallet/blob/main/docs/Guidelines.md).
                - [x] I confirm that I've run the code locally and everything works as expected.
                - [x] My PR includes only the necessary changes to fix the issue (i.e., no unnecessary files or lines of code are changed).
                - [x] 🎬 I've attached a **screen recording** of using the new code to the next paragraph (if applicable).

                ## What's changed?
                 - The new name reflects the full-featured nature of the application
            """.trimIndent(),
            expectedResult = Either.Left(ClosesIssueAnalyzer.MissingClosesProblem),
        ),
        INVALID_DEFAULT_TEMPLATE(
            prDescription = """
                ## Pull request (PR) checklist
                Please check if your pull request fulfills the following requirements:
                <!--💡 Tip: Tick checkboxes like this: [x] 💡-->
                - [ ] I've read the [Contribution Guidelines](https://github.com/Ivy-Apps/ivy-wallet/blob/main/CONTRIBUTING.md) and my PR doesn't break the rules.
                - [ ] I've read and understand the [Developer Guidelines](https://github.com/Ivy-Apps/ivy-wallet/blob/main/docs/Guidelines.md).
                - [ ] I confirm that I've run the code locally and everything works as expected.
                - [ ] My PR includes only the necessary changes to fix the issue (i.e., no unnecessary files or lines of code are changed).
                - [ ] 🎬 I've attached a **screen recording** of using the new code to the next paragraph (if applicable).
                ## Screen recording of interacting with your changes:
                <!--💡 Tip: Drag & drop the video here. 💡-->

                ## What's changed?
                Describe with a few bullets **what's new:**
                <!--💡 Tip: After each more important point leave one line empty and show your changes in markdown table with screenshots or screen recordings replacing {media}. In the end, it should look like this: 💡-->
                - I've fixed...

                Before|After
                ---------|---------
                {media}|{media}
                {media}|{media}
                - ...
                - ...
                ## Risk factors
                **What may go wrong if we merge your PR?**
                - ...
                - ...

                **In what cases won't your code work?**
                - ...
                - ...
                ## Does this PR close any GitHub issues? (do not delete)
                - Closes #{ISSUE_NUMBER}
                <!--❗For example: - Closes #123 ❗-->
                <!--💡 Tip: Replace {ISSUE_NUMBER} with the number of Ivy Wallet ISSUE (https://github.com/Ivy-Apps/ivy-wallet/issues)(❗NOT PR❗) which this pull request fixes. If done correctly, you'll see the issue title linked on PR preview. 💡-->
                <!--💡 Tip: Multiple issues:
                - Closes #{ISSUE_NUMBER_1}, closes #{ISSUE_NUMBER_2}, closes #{ISSUE_NUMBER_3}

                If the PR doesn't close any GitHub issues, type "Closes N/A" to pass the CI check.
                💡-->
                ## Troubleshooting CI failures ❌
                Pull request checks failing? Read our [CI Troubleshooting guide](https://github.com/Ivy-Apps/ivy-wallet/blob/main/docs/CI-Troubleshooting.md).
            """.trimIndent(),
            expectedResult = Either.Left(ClosesIssueAnalyzer.MissingClosesProblem),
        ),
        VALID_TEMPLATE_CLOSES_NUMBER(
            prDescription = """
                ## Pull request (PR) checklist
                Please check if your pull request fulfills the following requirements:
                <!--💡 Tip: Tick checkboxes like this: [x] 💡-->
                - [ ] I've read the [Contribution Guidelines](https://github.com/Ivy-Apps/ivy-wallet/blob/main/CONTRIBUTING.md) and my PR doesn't break the rules.
                - [ ] I've read and understand the [Developer Guidelines](https://github.com/Ivy-Apps/ivy-wallet/blob/main/docs/Guidelines.md).
                - [ ] I confirm that I've run the code locally and everything works as expected.
                - [ ] My PR includes only the necessary changes to fix the issue (i.e., no unnecessary files or lines of code are changed).
                - [ ] 🎬 I've attached a **screen recording** of using the new code to the next paragraph (if applicable).

                ## Screen recording of interacting with your changes:
                <!--💡 Tip: Drag & drop the video here. 💡-->

                ## What's changed?
                Describe with a few bullets **what's new:**
                - I've fixed...
                - 

                Before|After
                ---------|---------
                {media}|{media}
                {media}|{media}

                ## Risk factors
                **What may go wrong if we merge your PR?**
                - ...
                - ...

                **In what cases won't your code work?**
                - ...
                - ...

                ## Does this PR close any GitHub issues? (do not delete)
                - Closes #123

                <!--❗For example: - Closes #123 ❗-->
                <!--💡 Tip: Replace {ISSUE_NUMBER} with the number of Ivy Wallet ISSUE (https://github.com/Ivy-Apps/ivy-wallet/issues)(❗NOT PR❗) which this pull request fixes. If done correctly, you'll see the issue title linked on PR preview. 💡-->
                <!--💡 Tip: Multiple issues:
                - Closes #{ISSUE_NUMBER_1}, closes #{ISSUE_NUMBER_2}, closes #{ISSUE_NUMBER_3}

                If the PR doesn't close any GitHub issues, type "Closes N/A" to pass the CI check.
                💡-->

                ## Troubleshooting GitHub Actions (CI) failures ❌
                Pull request checks failing? Read our [CI Troubleshooting guide](https://github.com/Ivy-Apps/ivy-wallet/blob/main/docs/CI-Troubleshooting.md).
            """.trimIndent(),
            expectedResult = Either.Right(Unit)
        ),
        VALID_CLOSES_NA(
            prDescription = "Closes N/A",
            expectedResult = Either.Right(Unit),
        )
    }

    @Test
    fun `validate PR description check`(
        @TestParameter testCase: PRDescTestCase,
    ) {
        // When
        val result = analyzer.analyze(testCase.prDescription)

        // Then
        result shouldBe testCase.expectedResult
    }
}