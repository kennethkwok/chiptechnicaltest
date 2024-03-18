package com.kennethkwok.chiptechnicaltest.di

import com.kennethkwok.chiptechnicaltest.repository.DogRepository
import com.kennethkwok.chiptechnicaltest.repository.DogRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Binds
    @Singleton
    fun bindsDogRepository(dogRepository: DogRepositoryImpl): DogRepository
}
