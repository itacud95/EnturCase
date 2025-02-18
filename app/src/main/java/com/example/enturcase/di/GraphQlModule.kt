package com.example.enturcase.di

import com.example.enturcase.GraphQLClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object GraphQlModule {

    @Provides
    @Singleton
    fun provideGraphQlClient(): GraphQLClient {
        return GraphQLClient
    }
}
