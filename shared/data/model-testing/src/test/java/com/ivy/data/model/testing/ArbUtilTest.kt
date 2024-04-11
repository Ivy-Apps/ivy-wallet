package com.ivy.data.model.testing

import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ArbUtilTest {
    @Test
    fun `generates arb NotBlankTrimmedStrings`() = runTest {
        forAll(Arb.notBlankTrimmedString()) {
            NotBlankTrimmedString.from(it.value).isRight()
        }
    }

    @Test
    fun `generates arb PositiveDoubles`() = runTest {
        forAll(Arb.positiveDoubleExact()) {
            it.value >= 0.0 && it.value.isFinite()
        }
    }

    @Test
    fun `arb positive double respects max param`() = runTest {
        forAll(Arb.positiveDoubleExact(max = 10.0)) {
            it.value <= 10.0
        }
    }

    @Test
    fun `arb maybe handles both cases`() = runTest {
        // given
        var nonNullCaseGenerated = false
        var nullCaseGenerated = false

        // when
        forAll(Arb.maybe(Arb.int())) {
            if (it != null) {
                nonNullCaseGenerated = true
            } else {
                nullCaseGenerated = true
            }
            true
        }

        // then
        nonNullCaseGenerated shouldBe true
        nullCaseGenerated shouldBe true
    }

    @Test
    fun `generates arb IconAsset`() = runTest {
        forAll(Arb.iconAsset()) {
            IconAsset.from(it.id).isRight()
        }
    }
}