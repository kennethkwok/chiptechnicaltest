package com.kennethkwok.chiptechnicaltest.di

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.test.FakeImageLoaderEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@TestInstallIn(components = [SingletonComponent::class],
    replaces = [ImageLoaderModule::class])
@Module
object FakeImageLoaderModule {

    @OptIn(ExperimentalCoilApi::class)
    @Singleton
    @Provides
    fun providesFakeImageLoader(@ApplicationContext appContext: Context): ImageLoader {
        val engine = FakeImageLoaderEngine.Builder()
            .intercept("https://www.google.com", ColorDrawable(Color.RED))
            .intercept("https://www.apple.com", ColorDrawable(Color.GREEN))
            .default(ColorDrawable(Color.BLUE))
            .build()

        return ImageLoader.Builder(appContext)
            .components { add(engine) }
            .build()
    }
}
