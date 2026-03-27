package com.example.genaitraining.di

import com.example.genaitraining.data.repository.SnowtoothRepositoryImpl
import com.example.genaitraining.domain.repository.SnowtoothRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindSnowtoothRepository(
        snowtoothRepositoryImpl: SnowtoothRepositoryImpl
    ): SnowtoothRepository
}
