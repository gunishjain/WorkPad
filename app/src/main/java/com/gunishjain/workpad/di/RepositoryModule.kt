package com.gunishjain.workpad.di

import com.gunishjain.workpad.data.repository.AuthRepositoryImpl
import com.gunishjain.workpad.data.repository.PageRepositoryImpl
import com.gunishjain.workpad.domain.repository.AuthRepository
import com.gunishjain.workpad.domain.repository.PageRepository
import com.gunishjain.workpad.domain.repository.SyncRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ) : AuthRepository

    @Binds
    @Singleton
    abstract fun bindPageRepository(
        impl: PageRepositoryImpl
    ) : PageRepository

    @Binds
    @Singleton
    abstract fun bindSyncRepository(
        impl: SyncRepository
    ) : SyncRepository

}