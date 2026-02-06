package com.example.app.di

import com.example.data.repository.MockWerewolfRepository
import com.example.domain.repository.WerewolfRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

//    @Binds
//    @Singleton
//    abstract fun bindWerewolfRepository(
//        networkRepository: NetworkWerewolfRepository
//    ): WerewolfRepository

    @Binds
    @Singleton
    abstract fun bindWerewolfRepository(
        mockRepository: MockWerewolfRepository
    ): WerewolfRepository
}