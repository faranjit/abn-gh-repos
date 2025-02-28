package com.faranjit.ghrepos.data.datasource

import com.faranjit.ghrepos.data.api.GithubApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RemoteDataSourceTest {

    private lateinit var dataSource: RemoteDataSource

    @Test
    fun `getRepos should return repos from api`() = runTest {
        // Given
        val api: GithubApi = FakeGithubApi()
        dataSource = RemoteDataSource(api)

        val page = 1
        val perPage = 10
        val repos = api.getRepos(page, perPage)

        // When
        val result = dataSource.getRepos(page, perPage)

        // Then
        assertEquals(repos, result)
        assertEquals(perPage, result.size)
    }

    @Test(expected = RuntimeException::class)
    fun `getRepos should throw exception when api call fails`() = runTest {
        // Given
        val page = 1
        val perPage = 10
        val api: GithubApi = FakeGithubApi(true)
        dataSource = RemoteDataSource(api)

        // When
        dataSource.getRepos(page, perPage)
    }
}
