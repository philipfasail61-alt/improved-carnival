package com.animegen.app.di

import com.animegen.app.data.remote.CloudService
import com.animegen.app.data.remote.DalleService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Singleton
    @Provides
    fun provideDalleService(retrofit: Retrofit): DalleService {
        return retrofit.create(DalleService::class.java)
    }

    @Singleton
    @Provides
    fun provideCloudService(retrofit: Retrofit): CloudService {
        return retrofit.create(CloudService::class.java)
    }
}
