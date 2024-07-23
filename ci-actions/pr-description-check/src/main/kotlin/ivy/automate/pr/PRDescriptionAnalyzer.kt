package ivy.automate.pr

import arrow.core.Either

interface PRDescriptionAnalyzer {
    fun analyze(prDescription: String): Either<String, Unit>
}