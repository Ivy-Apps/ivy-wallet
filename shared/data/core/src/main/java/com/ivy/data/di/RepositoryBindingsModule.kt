package com.ivy.data.di

import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.CategoryRepository
import com.ivy.data.repository.CurrencyRepository
import com.ivy.data.repository.ExchangeRatesRepository
import com.ivy.data.repository.TagsRepository
import com.ivy.data.repository.TransactionRepository
import com.ivy.data.repository.impl.AccountRepositoryImpl
import com.ivy.data.repository.impl.CategoryRepositoryImpl
import com.ivy.data.repository.impl.CurrencyRepositoryImpl
import com.ivy.data.repository.impl.ExchangeRatesRepositoryImpl
import com.ivy.data.repository.impl.TagsRepositoryImpl
import com.ivy.data.repository.impl.TransactionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindingsModule {
    @Binds
    abstract fun bindAccountRepo(repo: AccountRepositoryImpl): AccountRepository

    @Binds
    abstract fun bindCategoryRepo(repo: CategoryRepositoryImpl): CategoryRepository

    @Binds
    abstract fun bindTransactionRepo(repo: TransactionRepositoryImpl): TransactionRepository

    @Binds
    abstract fun bindExchangeRatesRepo(repo: ExchangeRatesRepositoryImpl): ExchangeRatesRepository

    @Binds
    abstract fun bindTagsRepo(repo: TagsRepositoryImpl): TagsRepository

    @Binds
    abstract fun bindCurrencyRepo(repo: CurrencyRepositoryImpl): CurrencyRepository
}
