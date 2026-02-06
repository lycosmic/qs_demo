package com.example.app.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataParserModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()
}


