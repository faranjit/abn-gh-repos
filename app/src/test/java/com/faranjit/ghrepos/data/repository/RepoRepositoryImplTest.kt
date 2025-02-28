package com.faranjit.ghrepos.data.repository

import android.util.Log
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.faranjit.ghrepos.ConnectivityChecker
import com.faranjit.ghrepos.TestConnectivityChecker
import com.faranjit.ghrepos.createRepoEntities
import com.faranjit.ghrepos.data.FakePagingSource
import com.faranjit.ghrepos.data.FakeRepoPagerFactory
import com.faranjit.ghrepos.data.datasource.LocalDataSource
import com.faranjit.ghrepos.data.datasource.RemoteRepoMediator
import com.faranjit.ghrepos.data.db.entity.RepoEntity
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RepoRepositoryImplTest {

    private lateinit var connectivityChecker: ConnectivityChecker
    private lateinit var localDataSource: LocalDataSource
    private lateinit var remoteMediator: RemoteRepoMediator
    private lateinit var pagingConfig: PagingConfig
    private lateinit var repository: RepoRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.isLoggable(any(), any()) } returns false

        connectivityChecker = mockk()
        localDataSource = mockk()
        remoteMediator = mockk()
        pagingConfig = PagingConfig(10)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getRepos when network is available should return repos with remote mediator`() = runTest {
        // Given
        val connectivityChecker = TestConnectivityChecker(true)
        val entities = createRepoEntities(pagingConfig.pageSize)
        val pagerFactory = FakeRepoPagerFactory(entities)

        repository = RepoRepositoryImpl(
            connectivityChecker,
            localDataSource,
            remoteMediator,
            pagingConfig,
            pagerFactory
        )

        coEvery { localDataSource.getAllRepos() } returns FakePagingSource(entities)

        // When
        val result = repository.getRepos().first()
        val repoList = (result as? PagingData<RepoEntity>)?.let { pagingData ->
            val differ = AsyncPagingDataDiffer(
                diffCallback = RepoDiffCallback(),
                updateCallback = NoopListCallback(),
                workerDispatcher = testDispatcher
            )
            differ.submitData(pagingData)
            differ.snapshot().items
        } ?: emptyList()

        // Then
        assertEquals(pagingConfig.pageSize, repoList.size)
        repoList.forEachIndexed { index, repo ->
            assertEquals(entities[index].id, repo.id)
            assertEquals(entities[index].name, repo.name)
            assertEquals(entities[index].fullName, repo.fullName)
            assertEquals(entities[index].description, repo.description)
        }
    }

    @Test
    fun `getRepos when network is unavailable should return repos without remote mediator`() =
        runTest {
            // Given
            val connectivityChecker = TestConnectivityChecker(false)
            val entities = createRepoEntities(pagingConfig.pageSize)
            val pagerFactory = FakeRepoPagerFactory(entities)

            repository = RepoRepositoryImpl(
                connectivityChecker,
                localDataSource,
                remoteMediator,
                pagingConfig,
                pagerFactory
            )

            coEvery { localDataSource.getAllRepos() } returns FakePagingSource(entities)

            // When
            val result = repository.getRepos().first()
            val repoList = (result as? PagingData<RepoEntity>)?.let { pagingData ->
                val differ = AsyncPagingDataDiffer(
                    diffCallback = RepoDiffCallback(),
                    updateCallback = NoopListCallback(),
                    workerDispatcher = testDispatcher
                )
                differ.submitData(pagingData)
                differ.snapshot().items
            } ?: emptyList()

            // Then
            assertEquals(pagingConfig.pageSize, repoList.size)
            repoList.forEachIndexed { index, repo ->
                assertEquals(entities[index].id, repo.id)
                assertEquals(entities[index].name, repo.name)
                assertEquals(entities[index].fullName, repo.fullName)
                assertEquals(entities[index].description, repo.description)
            }
        }
}

private class RepoDiffCallback : DiffUtil.ItemCallback<RepoEntity>() {

    override fun areItemsTheSame(oldItem: RepoEntity, newItem: RepoEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RepoEntity, newItem: RepoEntity): Boolean {
        return oldItem == newItem
    }
}

private class NoopListCallback : ListUpdateCallback {
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
}
