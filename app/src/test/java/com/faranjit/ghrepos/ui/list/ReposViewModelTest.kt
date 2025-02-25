package com.faranjit.ghrepos.ui.list

import com.faranjit.ghrepos.domain.FetchReposUseCase
import com.faranjit.ghrepos.domain.model.Repo
import com.faranjit.ghrepos.domain.model.RepoOwner
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReposViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    private val fetchReposUseCase: FetchReposUseCase = mockk()

    private val viewModel = ReposViewModel(fetchReposUseCase)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchRepos should call use case with username`() = runTest {
        // given
        val username = "faranjit"
        val repos = listOf(createRepo())
        coEvery { fetchReposUseCase(username) } returns repos

        // when
        viewModel.fetchRepos(username)

        // then
        coVerify { fetchReposUseCase(username) }
    }

    private fun createRepo() = Repo(
        id = 1L,
        name = "test-repo",
        fullName = "faranjit/test-repo",
        description = "Test repository",
        starsCount = 10,
        owner = RepoOwner(
            id = 1L,
            login = "faranjit",
            avatarUrl = "https://avatar.url"
        ),
        htmlUrl = "https://github.com/faranjit/test-repo",
        visibility = "public"
    )
}
