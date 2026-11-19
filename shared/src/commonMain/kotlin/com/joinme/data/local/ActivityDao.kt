package com.joinme.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Query("SELECT * FROM activities")
    fun observeAll(): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<ActivityEntity>)

    @Query("DELETE FROM activities")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(activities: List<ActivityEntity>) {
        deleteAll()
        insertAll(activities)
    }

    @Query("SELECT * FROM sync_info WHERE id = 1")
    suspend fun getSyncInfo(): SyncInfoEntity?

    @Query("SELECT * FROM sync_info WHERE id = 1")
    fun observeSyncInfo(): Flow<SyncInfoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSyncInfo(info: SyncInfoEntity)

    @Query("DELETE FROM sync_info")
    suspend fun deleteSyncInfo()
}
