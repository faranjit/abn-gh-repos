package com.faranjit.ghrepos.di

import androidx.paging.PagingConfig
import com.faranjit.ghrepos.ConnectivityChecker
import com.faranjit.ghrepos.data.DefaultPagerFactory
import com.faranjit.ghrepos.data.api.GithubApi
import com.faranjit.ghrepos.data.datasource.LocalDataSource
import com.faranjit.ghrepos.data.datasource.RemoteDataSource
import com.faranjit.ghrepos.data.datasource.RemoteRepoMediator
import com.faranjit.ghrepos.data.db.AppDatabase
import com.faranjit.ghrepos.data.db.RemoteKeysDao
import com.faranjit.ghrepos.data.db.RepoDao
import com.faranjit.ghrepos.data.repository.RepoRepository
import com.faranjit.ghrepos.data.repository.RepoRepositoryImpl
import com.faranjit.ghrepos.domain.FetchReposUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRemoteDataSource(api: GithubApi): RemoteDataSource {
        return RemoteDataSource(api)
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(
        appDatabase: AppDatabase,
        repoDao: RepoDao,
        remoteKeysDao: RemoteKeysDao
    ): LocalDataSource {
        return LocalDataSource(
            database = appDatabase,
            repoDao = repoDao,
            remoteKeysDao = remoteKeysDao
        )
    }

    @Provides
    @Singleton
    fun provideRemoteMediator(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource
    ): RemoteRepoMediator {
        return RemoteRepoMediator(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource
        )
    }

    @Provides
    @Singleton
    fun provideRepoRepository(
        connectivityChecker: ConnectivityChecker,
        remoteMediator: RemoteRepoMediator,
        localDataSource: LocalDataSource,
    ): RepoRepository {
        return RepoRepositoryImpl(
            connectivityChecker = connectivityChecker,
            localDataSource = localDataSource,
            remoteMediator = remoteMediator,
            pagingConfig = PagingConfig(20),
            pagerFactory = DefaultPagerFactory(),
        )
    }

    @Provides
    @Singleton
    fun provideFetchReposUseCase(repository: RepoRepository): FetchReposUseCase {
        return FetchReposUseCase(repository)
    }
}
