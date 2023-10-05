package com.ivy.data.testing

import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.NotBlankTrimmedString
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.util.UUID

class FakeCategoryRepositoryTest : FreeSpec({
    fun newRepository() = FakeCategoryRepository()

    "find all" - {
        "not deleted" {
            // given
            val repository = newRepository()
            val id1 = CategoryId(UUID.randomUUID())
            val id2 = CategoryId(UUID.randomUUID())
            val categories = listOf(
                Category(
                    name = NotBlankTrimmedString("Home"),
                    color = ColorInt(42),
                    icon = null,
                    orderNum = 1.0,
                    removed = false,
                    lastUpdated = Instant.EPOCH,
                    id = id1
                ),
                Category(
                    name = NotBlankTrimmedString("Fun"),
                    color = ColorInt(42),
                    icon = null,
                    orderNum = 2.0,
                    removed = true,
                    lastUpdated = Instant.EPOCH,
                    id = id2
                )
            )

            // when
            repository.saveMany(categories)
            val res = repository.findAll(false)

            // then
            res shouldBe listOf(
                Category(
                    name = NotBlankTrimmedString("Home"),
                    color = ColorInt(42),
                    icon = null,
                    orderNum = 1.0,
                    removed = false,
                    lastUpdated = Instant.EPOCH,
                    id = id1
                )
            )
        }

        "deleted" {

        }

        "empty list" {

        }
    }
})