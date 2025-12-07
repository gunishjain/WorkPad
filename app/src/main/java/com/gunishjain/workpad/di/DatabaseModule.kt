package com.gunishjain.workpad.di

import android.content.Context
import androidx.room.Room
import com.gunishjain.workpad.WorkPad
import com.gunishjain.workpad.data.local.WorkPadDao
import com.gunishjain.workpad.data.local.WorkPadDatabase
import com.gunishjain.workpad.di.qualifiers.DatabaseName
import com.gunishjain.workpad.utils.AppConstant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @DatabaseName
    @Provides
    fun provideDatabaseName(): String = AppConstant.DATABASE_NAME


    @Provides
    @Singleton
    fun provideWorkPadDatabase(
        @ApplicationContext context: Context,
        @DatabaseName databaseName: String
    ): WorkPadDatabase {
        return Room.databaseBuilder(
            context,
            WorkPadDatabase::class.java,
            databaseName
        ).build()
    }

    @Provides
    @Singleton
    fun provideWorkPadDao(database: WorkPadDatabase): WorkPadDao {
        return database.workPadDao()
    }
}