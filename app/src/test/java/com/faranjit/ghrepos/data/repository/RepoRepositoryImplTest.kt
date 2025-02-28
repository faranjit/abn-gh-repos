package com.faranjit.ghrepos.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.faranjit.ghrepos.ConnectivityChecker
import com.faranjit.ghrepos.TestConnectivityChecker
import com.faranjit.ghrepos.data.FakePagingSource
import com.faranjit.ghrepos.data.PagerFactory
import com.faranjit.ghrepos.data.datasource.LocalDataSource
import com.faranjit.ghrepos.data.datasource.RemoteRepoMediator
import com.faranjit.ghrepos.data.db.entity.OwnerEntity
import com.faranjit.ghrepos.data.db.entity.RepoEntity
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalPagingApi::class)
class RepoRepositoryImplTest {

    private lateinit var connectivityChecker: ConnectivityChecker
    private lateinit var localDataSource: LocalDataSource
    private lateinit var remoteMediator: RemoteRepoMediator
    private lateinit var pagingConfig: PagingConfig
    private lateinit var pagerFactory: PagerFactory
    private lateinit var repository: RepoRepository

    @Before
    fun setup() {
        connectivityChecker = mockk()
        localDataSource = mockk()
        remoteMediator = mockk()
        pagingConfig = PagingConfig(10)
        pagerFactory = mockk()
    }

    @Test
    fun `getRepos when network is available should return repos from Pager with remote mediator`() =
        runTest {
            // Given
            val connectivityChecker: ConnectivityChecker = TestConnectivityChecker(true)
            repository = RepoRepositoryImpl(
                connectivityChecker,
                localDataSource,
                remoteMediator,
                pagingConfig,
                pagerFactory
            )

            val entities = (1..pagingConfig.pageSize).map { createRepoEntity(it.toLong()) }
            val expectedFlow = flowOf(PagingData.from(entities))

            coEvery { localDataSource.getAllRepos() } returns FakePagingSource(entities)
            every {
                pagerFactory.createPager(
                    config = pagingConfig,
                    remoteMediator = remoteMediator,
                    pagingSourceFactory = any()
                )
            } returns mockk {
                every { flow } returns expectedFlow
            }

            // When
            val actual = repository.getRepos(1, pagingConfig.pageSize).first()
            val expectedData = expectedFlow.first()

            // Then
            assertEquals(expectedData, actual)
        }

    @Test
    fun `getRepos when network is unavailable should return repos from Pager without remote mediator`() =
        runTest {
            // Given
            val connectivityChecker: ConnectivityChecker = TestConnectivityChecker(false)
            repository = RepoRepositoryImpl(
                connectivityChecker,
                localDataSource,
                remoteMediator,
                pagingConfig,
                pagerFactory
            )

            val entities = (1..pagingConfig.pageSize).map { createRepoEntity(it.toLong()) }

            val expectedFlow = flowOf(PagingData.from(entities))

            coEvery { localDataSource.getAllRepos() } returns FakePagingSource(entities)
            every {
                pagerFactory.createPager<Int, RepoEntity>(
                    config = pagingConfig,
                    remoteMediator = null,
                    pagingSourceFactory = any()
                )
            } returns mockk {
                every { flow } returns expectedFlow
            }

            // When
            val actual = repository.getRepos(1, pagingConfig.pageSize).first()
            val expectedData = expectedFlow.first()

            // Then
            assertEquals(expectedData, actual)
        }

    private fun createRepoEntity(id: Long) = RepoEntity(
        id = id,
        repoId = id,
        name = "test",
        fullName = "faranjit/test",
        description = "Test repository",
        stars = 10,
        owner = OwnerEntity(
            ownerId = 1L,
            login = "faranjit",
            avatarUrl = "https://avatar.url"
        ),
        htmlUrl = "https://github.com/faranjit/test",
        visibility = "public"
    )
}
