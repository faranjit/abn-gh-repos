package com.faranjit.ghrepos.data.datasource

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.faranjit.ghrepos.createRepoEntities
import com.faranjit.ghrepos.createRepoEntity
import com.faranjit.ghrepos.data.db.AppDatabase
import com.faranjit.ghrepos.data.db.RemoteKeysDao
import com.faranjit.ghrepos.data.db.RepoDao
import com.faranjit.ghrepos.data.db.entity.RemoteKeysEntity
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
        val repos = createRepoEntities(3)

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
        val repo = createRepoEntity(1)
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
