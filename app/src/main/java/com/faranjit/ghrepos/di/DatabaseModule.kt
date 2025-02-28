package com.faranjit.ghrepos.di

import android.content.Context
import androidx.room.Room
import com.faranjit.ghrepos.data.db.AppDatabase
import com.faranjit.ghrepos.data.db.RemoteKeysDao
import com.faranjit.ghrepos.data.db.RepoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "gh_repos.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRepoDao(database: AppDatabase): RepoDao {
        return database.repoDao()
    }

    @Provides
    @Singleton
    fun provideRemoteKeysDao(database: AppDatabase): RemoteKeysDao {
        return database.remoteKeysDao()
    }
}
