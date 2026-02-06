package com.example.app.di

import com.example.data.repository.MockAiActorRepository
import com.example.domain.repository.AiActorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // TODO: 把 MockAiActorRepository 改成 RealAiActorRepository
    @Binds
    @Singleton
    abstract fun bindAiActorRepository(
        impl: MockAiActorRepository
    ): AiActorRepository
}