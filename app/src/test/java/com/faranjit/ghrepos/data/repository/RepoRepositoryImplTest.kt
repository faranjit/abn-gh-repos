package com.faranjit.ghrepos.data.repository

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.faranjit.ghrepos.createRepoEntities
import com.faranjit.ghrepos.data.FakePagingSource
import com.faranjit.ghrepos.data.FakeRepoPagerFactory
import com.faranjit.ghrepos.data.datasource.LocalDataSource
import com.faranjit.ghrepos.data.datasource.RemoteRepoMediator
import com.faranjit.ghrepos.data.db.entity.RepoEntity
import io.mockk.coEvery
import io.mockk.mockk
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

    private lateinit var localDataSource: LocalDataSource
    private lateinit var remoteMediator: RemoteRepoMediator
    private lateinit var pagingConfig: PagingConfig
    private lateinit var repository: RepoRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        localDataSource = mockk()
        remoteMediator = mockk()
        pagingConfig = PagingConfig(10)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getRepos should return repos from pager factory`() = runTest {
        // Given
        val entities = createRepoEntities(pagingConfig.pageSize)
        val pagerFactory = FakeRepoPagerFactory(entities)

        repository = RepoRepositoryImpl(
            localDataSource,
            remoteMediator,
            pagingConfig,
            pagerFactory
        )

        coEvery { localDataSource.getAllRepos() } returns FakePagingSource(entities)

        // When
        val result = repository.getRepos().first()
        val repoList = result.let { pagingData ->
            val differ = AsyncPagingDataDiffer(
                diffCallback = RepoDiffCallback(),
                updateCallback = NoopListCallback(),
                workerDispatcher = testDispatcher
            )
            differ.submitData(pagingData)
            differ.snapshot().items
        }

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
