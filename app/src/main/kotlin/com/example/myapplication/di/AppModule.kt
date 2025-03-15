package com.example.myapplication.di

import android.content.Context
import coil.ImageLoader
import com.example.myapplication.data.repository.WalletRepository
import com.example.myapplication.data.repository.WalletRepositoryImpl
import com.example.myapplication.data.source.WalletDataSource
import com.example.myapplication.data.source.WalletDataSourceImpl
import com.example.myapplication.util.ImageLoaderUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        allowSpecialFloatingPointValues = true
        allowStructuredMapKeys = true
        prettyPrint = false
        useArrayPolymorphism = false
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoaderUtil.createImageLoader(context)
    }

    @Provides
    @Singleton
    fun provideWalletDataSource(json: Json): WalletDataSource {
        return WalletDataSourceImpl(json)
    }

    @Provides
    @Singleton
    fun provideWalletRepository(
        @ApplicationContext context: Context,
        dataSource: WalletDataSource
    ): WalletRepository {
        return WalletRepositoryImpl(context, dataSource)
    }
} 