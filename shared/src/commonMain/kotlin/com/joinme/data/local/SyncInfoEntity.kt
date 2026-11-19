package com.joinme.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Single row (id = 1) that remembers when the activities were last downloaded. */
@Entity(tableName = "sync_info")
data class SyncInfoEntity(
    @PrimaryKey val id: Int = 1,
    val lastUpdated: String,
)
