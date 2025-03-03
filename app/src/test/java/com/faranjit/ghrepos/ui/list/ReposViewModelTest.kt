package com.faranjit.ghrepos.ui.list

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.faranjit.ghrepos.data.datasource.NoRepoFoundException
import com.faranjit.ghrepos.domain.FetchReposUseCase
import com.faranjit.ghrepos.domain.NetworkConnectivityMonitor
import com.faranjit.ghrepos.domain.model.Repo
import com.faranjit.ghrepos.domain.model.RepoOwner
import com.faranjit.ghrepos.domain.model.RepoVisibility
import com.faranjit.ghrepos.ui.ReposUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okio.IOException
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReposViewModelTest {

    private lateinit var viewModel: ReposViewModel

    private val fetchReposUseCase: FetchReposUseCase = mockk()
    private val networkMonitor: NetworkConnectivityMonitor = mockk()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        coEvery { networkMonitor.startMonitoring() } returns Unit
        coEvery { networkMonitor.stopMonitoring() } returns Unit
        coEvery { networkMonitor.isOnline } returns MutableStateFlow(true)

        viewModel = ReposViewModel(networkMonitor, fetchReposUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when network is restored, should show connection restored state`() = runTest {
        // Given
        val isOnline = MutableStateFlow(false)
        coEvery { networkMonitor.isOnline } returns isOnline

        // When
        viewModel = ReposViewModel(networkMonitor, fetchReposUseCase)
        isOnline.value = true

        // Then
        assertEquals(ReposUiState.ConnectionRestored, viewModel.uiState.value)
    }

    @Test
    fun `when loading state and not success, should show loading`() {
        // Given
        val loadStates = CombinedLoadStates(
            refresh = LoadState.Loading,
            prepend = LoadState.NotLoading(false),
            append = LoadState.NotLoading(false),
            source = LoadStates(
                refresh = LoadState.Loading,
                prepend = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false)
            ),
            mediator = null
        )

        // When
        viewModel.handleLoadStates(loadStates)

        // Then
        assertEquals(ReposUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `when loading state and success, should not show loading`() = runTest {
        // Given
        val repos = listOf(createRepo())
        val pagingData = PagingData.from(repos)
        coEvery { fetchReposUseCase() } returns flowOf(pagingData)

        viewModel.fetchRepos()

        val loadStates = CombinedLoadStates(
            refresh = LoadState.Loading,
            prepend = LoadState.NotLoading(false),
            append = LoadState.NotLoading(false),
            source = LoadStates(
                refresh = LoadState.Loading,
                prepend = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false)
            ),
            mediator = null
        )

        // When
        viewModel.handleLoadStates(loadStates)

        // Then
        assertTrue(viewModel.uiState.value is ReposUiState.Success)
    }

    @Test
    fun `fetchRepos should return success`() = runTest(testDispatcher) {
        // Given
        val repos = listOf(createRepo())
        val pagingData = PagingData.from(repos)
        val flow = flowOf(pagingData)
        coEvery { fetchReposUseCase() } returns flow

        // When
        viewModel = ReposViewModel(networkMonitor, fetchReposUseCase)

        // Then
        assertTrue(viewModel.uiState.value is ReposUiState.Success)
        coVerify { fetchReposUseCase() }
    }

    @Test
    fun `when fetchRepos returns error retry should exist`() = runTest(testDispatcher) {
        // Given
        val exception = IOException("Network error")
        coEvery { fetchReposUseCase() } throws exception

        // When
        viewModel.fetchRepos()

        // Then
        assertEquals(
            ReposUiState.Error(
                message = "Network error",
                showRetry = true
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `handleLoadStates with loading should update uistate loading`() {
        // Given
        val loadStates = CombinedLoadStates(
            refresh = LoadState.Loading,
            prepend = LoadState.NotLoading(endOfPaginationReached = false),
            append = LoadState.NotLoading(endOfPaginationReached = false),
            source = LoadStates(
                refresh = LoadState.Loading,
                prepend = LoadState.NotLoading(endOfPaginationReached = false),
                append = LoadState.NotLoading(endOfPaginationReached = false)
            ),
            mediator = null
        )

        // When
        viewModel.handleLoadStates(loadStates)

        // Then
        assertEquals(ReposUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `handleLoadStates with error should update uistate error`() {
        // Given
        val exception = Exception("Unknown error")
        val loadStates = CombinedLoadStates(
            refresh = LoadState.Error(exception),
            prepend = LoadState.NotLoading(endOfPaginationReached = false),
            append = LoadState.NotLoading(endOfPaginationReached = false),
            source = LoadStates(
                refresh = LoadState.Error(exception),
                prepend = LoadState.NotLoading(endOfPaginationReached = false),
                append = LoadState.NotLoading(endOfPaginationReached = false)
            ),
            mediator = null
        )

        // When
        viewModel.handleLoadStates(loadStates)

        // Then
        assertEquals(
            ReposUiState.Error(
                message = "Unknown error",
                showRetry = false
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `when fetchRepos returns NoRepoFoundException should show appropriate error`() = runTest(testDispatcher) {
        // Given
        val exception = NoRepoFoundException(networkAvailable = false)
        coEvery { fetchReposUseCase() } throws exception

        // When
        viewModel.fetchRepos()

        // Then
        assertEquals(
            ReposUiState.Error(
                message = "No repositories available offline. Please connect to a network!",
                showRetry = true
            ),
            viewModel.uiState.value
        )
    }

    private fun createRepo() = Repo(
        id = 1L,
        name = "test",
        fullName = "test/test",
        description = "Test repo",
        starsCount = 10,
        owner = RepoOwner(
            id = 1L,
            login = "test",
            avatarUrl = "https://test.com"
        ),
        htmlUrl = "https://test.com",
        visibility = RepoVisibility.PUBLIC
    )
}
