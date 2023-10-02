package com.ivy.data.repository.impl

import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.mapper.AccountMapper
import com.ivy.data.source.LocalAccountDataSource
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class AccountRepositoryImplTest : FreeSpec({
    val dataSource = mockk<LocalAccountDataSource>()

    fun newRepository(): AccountRepository = AccountRepositoryImpl(
        mapper = AccountMapper(),
        dataSource = dataSource
    )

    "finds max order num" - {
        "no accounts" {
            // given
            coEvery { dataSource.findMaxOrderNum() } returns null
            val repository = newRepository()

            // when
            val orderNum = repository.findMaxOrderNum()

            // then
            orderNum shouldBe 0.0
        }

        "existing account" {
            // given
            coEvery { dataSource.findMaxOrderNum() } returns 42.0
            val repository = newRepository()

            // when
            val orderNum = repository.findMaxOrderNum()

            // then
            orderNum shouldBe 42.0
        }
    }
})