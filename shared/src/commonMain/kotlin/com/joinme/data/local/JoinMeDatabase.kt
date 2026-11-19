package com.joinme.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [ActivityEntity::class, SyncInfoEntity::class], version = 1)
@ConstructedBy(JoinMeDatabaseConstructor::class)
abstract class JoinMeDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
}

// Room generates the actual implementation of this object for each platform.
@Suppress("KotlinNoActualForExpect")
expect object JoinMeDatabaseConstructor : RoomDatabaseConstructor<JoinMeDatabase> {
    override fun initialize(): JoinMeDatabase
}
