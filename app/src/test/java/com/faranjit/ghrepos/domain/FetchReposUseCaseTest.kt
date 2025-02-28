package com.faranjit.ghrepos.domain

import androidx.paging.PagingData
import com.faranjit.ghrepos.data.db.entity.OwnerEntity
import com.faranjit.ghrepos.data.db.entity.RepoEntity
import com.faranjit.ghrepos.data.repository.RepoRepository
import com.faranjit.ghrepos.domain.model.Repo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FetchReposUseCaseTest {
    private val repository: RepoRepository = mockk()

    private val useCase = FetchReposUseCase(repository)

    @Test
    fun `invoke should return mapped paging data`() = runTest {
        // Given
        val repoEntity = createRepoEntity(1)
        val pagingData = PagingData.from(listOf(repoEntity))
        coEvery { repository.getRepos(1, 10) } returns flowOf(pagingData)

        // When
        val result = useCase(1, 10).first()

        // Then
        assert(result is PagingData<Repo>)
        coVerify { repository.getRepos(1, 10) }
    }

    @Test
    fun `invoke should use default parameters when not provided`() = runTest {
        // Given
        val pagingData = PagingData.from(emptyList<RepoEntity>())
        coEvery { repository.getRepos(1, 10) } returns flowOf(pagingData)

        // When
        useCase().first()

        // Then
        coVerify { repository.getRepos(1, 10) }
    }

    private fun createRepoEntity(id: Long) = RepoEntity(
        id = id,
        repoId = id,
        name = "test-repo",
        fullName = "faranjit/test-repo",
        description = "Test repository",
        stars = 10,
        owner = OwnerEntity(
            ownerId = 1L,
            login = "faranjit",
            avatarUrl = "https://avatar.url"
        ),
        htmlUrl = "https://github.com/faranjit/test-repo",
        visibility = "public"
    )
}
