package com.kennethkwok.chiptechnicaltest.di

import com.kennethkwok.chiptechnicaltest.repository.DogRepository
import com.kennethkwok.chiptechnicaltest.repository.FakeDogRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@TestInstallIn(components = [SingletonComponent::class],
    replaces = [RepositoryModule::class])
@Module
internal interface FakeRepositoryModule {

    @Singleton
    @Binds
    fun bindsDogRepository(fakeDogRepository: FakeDogRepositoryImpl): DogRepository
}
