package com.faranjit.ghrepos.di

import androidx.paging.PagingConfig
import com.faranjit.ghrepos.data.DefaultRepoPagerFactory
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
import com.faranjit.ghrepos.domain.NetworkConnectivityMonitor
import com.faranjit.ghrepos.ui.list.ReposIdlingResource
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
        networkMonitor: NetworkConnectivityMonitor,
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource
    ): RemoteRepoMediator {
        return RemoteRepoMediator(
            networkMonitor = networkMonitor,
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource
        )
    }

    @Provides
    @Singleton
    fun provideRepoRepository(
        remoteMediator: RemoteRepoMediator,
        localDataSource: LocalDataSource,
    ): RepoRepository {
        return RepoRepositoryImpl(
            localDataSource = localDataSource,
            remoteMediator = remoteMediator,
            pagingConfig = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 20,
            ),
            pagerFactory = DefaultRepoPagerFactory(),
        )
    }

    @Provides
    @Singleton
    fun provideFetchReposUseCase(repository: RepoRepository): FetchReposUseCase {
        return FetchReposUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideReposIdlingResource(): ReposIdlingResource {
        return ReposIdlingResource()
    }
}
