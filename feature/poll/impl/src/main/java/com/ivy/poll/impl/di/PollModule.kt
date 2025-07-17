package com.ivy.poll.impl.di

import com.ivy.poll.data.PollRepository
import com.ivy.poll.impl.data.PollRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PollModule {
  @Binds
  abstract fun bindPollRepository(impl: PollRepositoryImpl): PollRepository
}