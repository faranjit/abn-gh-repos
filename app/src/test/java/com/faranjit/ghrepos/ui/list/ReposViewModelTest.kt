package com.faranjit.ghrepos.ui.list

import androidx.paging.PagingData
import com.faranjit.ghrepos.domain.FetchReposUseCase
import com.faranjit.ghrepos.domain.model.Repo
import com.faranjit.ghrepos.domain.model.RepoOwner
import com.faranjit.ghrepos.domain.model.RepoVisibility
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReposViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    private val fetchReposUseCase: FetchReposUseCase = mockk()

    private lateinit var viewModel: ReposViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchRepos should return paging data from use case`() = runTest {
        // Given
        val repos = listOf(createRepo())
        val pagingData = PagingData.from(repos)
        val flow = flowOf(pagingData)
        coEvery { fetchReposUseCase() } returns flow
        viewModel = ReposViewModel(fetchReposUseCase)

        // When
        val result = viewModel.repos

        // Then
        coVerify { fetchReposUseCase() }
        assertNotNull(result.first())
    }

    private fun createRepo() = Repo(
        id = 1L,
        name = "test",
        fullName = "faranjit/test",
        description = "Test repository",
        starsCount = 10,
        owner = RepoOwner(
            id = 1L,
            login = "faranjit",
            avatarUrl = "https://avatar.url"
        ),
        htmlUrl = "https://github.com/faranjit/test",
        visibility = RepoVisibility.PUBLIC
    )
}
