package com.ivy.data.model.testing

import arrow.core.Some
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.property.forAll
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ArbCategoryTest {

    @Test
    fun `generates arb category`() = runTest {
        forAll(Arb.category()) {
            true
        }
    }

    @Test
    fun `arb category respects passed param`() = runTest {
        val categoryId = ModelFixtures.CategoryId

        checkAll(Arb.category(categoryId = Some(categoryId))) { category ->
            category.id shouldBe categoryId
        }
    }
}