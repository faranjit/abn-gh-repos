package com.faranjit.ghrepos.data.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.faranjit.ghrepos.createRepoEntity
import com.faranjit.ghrepos.data.db.entity.RemoteKeysEntity
import com.faranjit.ghrepos.data.db.entity.RepoEntity
import com.faranjit.ghrepos.data.model.OwnerResponse
import com.faranjit.ghrepos.data.model.RepoResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalPagingApi::class)
class RemoteRepoMediatorTest {
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var localDataSource: LocalDataSource
    private lateinit var mediator: RemoteRepoMediator

    @Before
    fun setup() {
        remoteDataSource = mockk()
        localDataSource = mockk()
        mediator = RemoteRepoMediator(remoteDataSource, localDataSource)
    }

    @Test
    fun `when load type is refresh, should clear data and insert new items`() = runTest {
        // Given
        val pageSize = 5
        val page = 1
        val total = 10
        val repos = createMockRepos(total)
        val pagingState = createPagingState(pageSize)
        val capturedKeys = slot<List<RemoteKeysEntity>>()
        val capturedRepos = slot<List<RepoEntity>>()

        coEvery { remoteDataSource.getRepos(page, pageSize) } returns repos
        coEvery { localDataSource.withTransaction(any<suspend () -> Unit>()) } coAnswers {
            firstArg<suspend () -> Unit>().invoke()
        }
        coEvery { localDataSource.clearRemoteKeys() } returns Unit
        coEvery { localDataSource.clearAllRepos() } returns Unit
        coEvery { localDataSource.insertRemoteKeys(capture(capturedKeys)) } returns Unit
        coEvery { localDataSource.insertRepos(capture(capturedRepos)) } returns Unit

        // When
        mediator.load(LoadType.REFRESH, pagingState)

        // Then
        coVerifyOrder {
            localDataSource.clearRemoteKeys()
            localDataSource.clearAllRepos()
            localDataSource.insertRemoteKeys(any())
            localDataSource.insertRepos(any())
        }

        with(capturedKeys.captured) {
            assertEquals(total, size)
            assertEquals(null, first().prevKey)
            assertEquals(page + 1, first().nextKey)
        }

        with(capturedRepos.captured) {
            assertEquals(total, size)
            assertEquals(0L, first().id)
            assertEquals((total - 1).toLong(), last().id)
        }
    }

    @Test
    fun `when load type is prepend, then return success with end of pagination`() = runTest {
        // Given
        val pagingState = createPagingState(30)

        // When
        val result = mediator.load(LoadType.PREPEND, pagingState)

        // Then
        assert(result is RemoteMediator.MediatorResult.Success)
        assert((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `when load type is append and remote key is null, then return success with not end of pagination`() =
        runTest {
            // Given
            val pagingState = createPagingState(30)
            coEvery { localDataSource.getRemoteKeyForRepoId(any()) } returns null

            // When
            val result = mediator.load(LoadType.APPEND, pagingState)

            // Then
            assert(result is RemoteMediator.MediatorResult.Success)
            assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        }

    @Test
    fun `when load type is append and nextKey is null, then return success with end of pagination`() =
        runTest {
            // Given
            val pagingState = createPagingState(
                30,
                listOf(
                    PagingSource.LoadResult.Page(
                        listOf(createRepoEntity(1)), 1, null
                    )
                )
            )
            coEvery { localDataSource.getRemoteKeyForRepoId(any()) } returns RemoteKeysEntity(
                repoId = 1,
                prevKey = 1,
                nextKey = null
            )

            // When
            val result = mediator.load(LoadType.APPEND, pagingState)

            // Then
            assert(result is RemoteMediator.MediatorResult.Success)
            assert((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        }

    @Test
    fun `when api call fails, then return error result`() = runTest {
        // Given
        val pagingState = createPagingState(30)
        val exception = RuntimeException("error")
        coEvery { remoteDataSource.getRepos(any(), any()) } throws exception

        // When
        val result = mediator.load(LoadType.REFRESH, pagingState)

        // Then
        assert(result is RemoteMediator.MediatorResult.Error)
        assertEquals(exception, (result as RemoteMediator.MediatorResult.Error).throwable)
    }

    @Test
    fun `when load type is append, should insert new items without clearing`() = runTest {
        // Given
        val pageSize = 5
        val page = 2
        val repos = createMockRepos(pageSize)
        val repoEntity = createRepoEntity(1)
        val pagingState = PagingState(
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = listOf(repoEntity),
                    prevKey = null,
                    nextKey = page
                )
            ),
            anchorPosition = 0,
            config = PagingConfig(pageSize = pageSize),
            leadingPlaceholderCount = 0
        )
        val remoteKey = RemoteKeysEntity(repoId = 1L, prevKey = 1, nextKey = page)
        val capturedKeys = slot<List<RemoteKeysEntity>>()
        val capturedRepos = slot<List<RepoEntity>>()

        coEvery { remoteDataSource.getRepos(page, pageSize) } returns repos
        coEvery { localDataSource.getRemoteKeyForRepoId(repoEntity.repoId) } returns remoteKey
        coEvery { localDataSource.withTransaction(any<suspend () -> Unit>()) } coAnswers {
            firstArg<suspend () -> Unit>().invoke()
        }
        coEvery { localDataSource.insertRemoteKeys(capture(capturedKeys)) } returns Unit
        coEvery { localDataSource.insertRepos(capture(capturedRepos)) } returns Unit

        // When
        mediator.load(LoadType.APPEND, pagingState)

        // Then
        coVerify(exactly = 0) {
            localDataSource.clearRemoteKeys()
            localDataSource.clearAllRepos()
        }

        with(capturedKeys.captured) {
            assertEquals(pageSize, size)
            assertEquals(page - 1, first().prevKey)
            assertEquals(page + 1, first().nextKey)
        }

        with(capturedRepos.captured) {
            assertEquals(pageSize, size)
            assertEquals(pageSize.toLong(), first().id)
            assertEquals(((pageSize * page) - 1).toLong(), last().id)
        }
    }

    private fun createPagingState(
        pageSize: Int,
        pages: List<PagingSource.LoadResult.Page<Int, RepoEntity>>? = null
    ) =
        PagingState(
            pages = pages ?: listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = pageSize),
            leadingPlaceholderCount = 0
        )

    private fun createMockRepos(size: Int) = List(size) { index ->
        RepoResponse(
            repoId = index.toLong(),
            name = "repo $index",
            fullName = "full name $index",
            description = "description $index",
            stars = index,
            owner = OwnerResponse(
                id = index.toLong(),
                login = "user $index",
                avatarUrl = "https://https://avatar$index.url.com"
            ),
            htmlUrl = "https://github.com/faranjit/repo$index",
            visibility = "public"
        )
    }
}
