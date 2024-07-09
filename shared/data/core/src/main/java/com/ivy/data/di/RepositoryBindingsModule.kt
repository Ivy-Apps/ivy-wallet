package com.ivy.data.di

import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.CategoryRepository
import com.ivy.data.repository.CurrencyRepository
import com.ivy.data.repository.ExchangeRatesRepository
import com.ivy.data.repository.LegalRepository
import com.ivy.data.repository.TagRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindingsModule {
    @Binds
    abstract fun bindAccountRepo(repo: AccountRepository): AccountRepository

    @Binds
    abstract fun bindCategoryRepo(repo: CategoryRepository): CategoryRepository

    @Binds
    abstract fun bindExchangeRatesRepo(repo: ExchangeRatesRepository): ExchangeRatesRepository

    @Binds
    abstract fun bindTagsRepo(repo: TagRepository): TagRepository

    @Binds
    abstract fun bindCurrencyRepo(repo: CurrencyRepository): CurrencyRepository

    @Binds
    abstract fun bindLegalRepo(repo: LegalRepository): LegalRepository
}
