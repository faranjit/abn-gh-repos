package com.faranjit.ghrepos.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.faranjit.ghrepos.data.db.entity.RemoteKeysEntity

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<RemoteKeysEntity>)

    @Query("SELECT * FROM RemoteKeysEntity WHERE repoId = :repoId")
    suspend fun getRemoteKeysForRepoId(repoId: Long): RemoteKeysEntity?

    @Query("DELETE FROM RemoteKeysEntity")
    suspend fun clearRemoteKeys()
}
