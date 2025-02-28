package com.faranjit.ghrepos.data.datasource

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.faranjit.ghrepos.data.db.AppDatabase
import com.faranjit.ghrepos.data.db.RemoteKeysDao
import com.faranjit.ghrepos.data.db.RepoDao
import com.faranjit.ghrepos.data.db.entity.OwnerEntity
import com.faranjit.ghrepos.data.db.entity.RemoteKeysEntity
import com.faranjit.ghrepos.data.db.entity.RepoEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalDataSourceTest {
    private lateinit var database: AppDatabase
    private lateinit var repoDao: RepoDao
    private lateinit var remoteKeysDao: RemoteKeysDao
    private lateinit var dataSource: LocalDataSource

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repoDao = database.repoDao()
        remoteKeysDao = database.remoteKeysDao()
        dataSource = LocalDataSource(database, repoDao, remoteKeysDao)
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun insertAndGetRepos() = runTest {
        // Given
        val repos = List(3) { index ->
            RepoEntity(
                id = index.toLong(),
                repoId = index.toLong(),
                name = "repo $index",
                fullName = "faranjit/repo$index",
                description = "description $index",
                stars = index * 2,
                owner = OwnerEntity(
                    ownerId = index.toLong(),
                    login = "trabzonspor",
                    avatarUrl = "https://avatar$index.url"
                ),
                htmlUrl = "https://repo$index.url",
                visibility = "public"
            )
        }

        // When
        dataSource.insertRepos(repos)

        val pagingSource = dataSource.getAllRepos()
        val page = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = 3,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(page is PagingSource.LoadResult.Page)
        assertEquals(repos, (page as PagingSource.LoadResult.Page).data)
    }

    @Test
    fun clearAllRepos() = runTest {
        // Given
        val repo = RepoEntity(
            id = 1L,
            repoId = 1L,
            name = "test-repo",
            fullName = "user/test-repo",
            description = "description",
            stars = 1,
            owner = OwnerEntity(
                ownerId = 61,
                login = "trabzonspor",
                avatarUrl = ""
            ),
            htmlUrl = "url",
            visibility = "public"
        )
        dataSource.insertRepos(listOf(repo))

        // When
        dataSource.clearAllRepos()

        val pagingSource = dataSource.getAllRepos()
        val page = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 0,
                loadSize = 1,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(page is PagingSource.LoadResult.Page)
        assertTrue((page as PagingSource.LoadResult.Page).data.isEmpty())
    }

    @Test
    fun insertAndGetRemoteKeys() = runTest {
        // Given
        val remoteKey = RemoteKeysEntity(
            repoId = 1L,
            prevKey = 1,
            nextKey = 2
        )

        // When
        dataSource.insertRemoteKeys(listOf(remoteKey))
        val result = dataSource.getRemoteKeyForRepoId(1L)

        // Then
        assertEquals(remoteKey, result)
    }

    @Test
    fun clearRemoteKeys() = runTest {
        // Given
        val remoteKey = RemoteKeysEntity(
            repoId = 1L,
            prevKey = 1,
            nextKey = 2
        )
        dataSource.insertRemoteKeys(listOf(remoteKey))

        // When
        dataSource.clearRemoteKeys()
        val result = dataSource.getRemoteKeyForRepoId(1L)

        // Then
        assertNull(result)
    }
}
