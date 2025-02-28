package com.faranjit.ghrepos.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import com.faranjit.ghrepos.data.db.entity.RemoteKeysEntity
import com.faranjit.ghrepos.data.db.entity.RepoEntity

@Dao
interface RepoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepos(repos: List<RepoEntity>)

    @Query("SELECT * FROM RepoEntity ORDER BY id ASC")
    fun getAllRepos(): PagingSource<Int, RepoEntity>

    @Query("DELETE FROM RepoEntity")
    suspend fun clearAllRepos()
}

@Database(
    entities = [RepoEntity::class, RemoteKeysEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}