package com.ivy.data.di

import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.impl.AccountRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindingsModule {
    @Binds
    abstract fun bindAccountRepository(repo: AccountRepositoryImpl): AccountRepository
}