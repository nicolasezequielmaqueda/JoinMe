package com.joinme.data

import com.joinme.data.local.ActivityDao
import com.joinme.data.local.ActivityEntity
import com.joinme.data.local.SyncInfoEntity
import com.joinme.data.local.toEntity
import com.joinme.model.Activity
import com.joinme.model.ActivityType
import com.joinme.model.ResidenceLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Privacy requirement: after the session ends, nothing of that user stays on the device. */
class ClearLocalDataTest {

    private val now = LocalDateTime(2026, 11, 18, 12, 0)

    private val activity = Activity(
        id = "a",
        type = ActivityType.FOOTBALL,
        date = LocalDate(2026, 11, 20),
        startTime = LocalTime(18, 0),
        durationMinutes = 60,
        location = ResidenceLocation.SPORTS_COURT,
        maxParticipants = 8,
        creatorId = "creator",
        participantIds = listOf("creator", "me"),
    )

    private class FakeActivityDao : ActivityDao {
        val activities = MutableStateFlow<List<ActivityEntity>>(emptyList())
        val syncInfo = MutableStateFlow<SyncInfoEntity?>(null)

        override fun observeAll(): Flow<List<ActivityEntity>> = activities

        override suspend fun insertAll(activities: List<ActivityEntity>) {
            this.activities.value = this.activities.value + activities
        }

        override suspend fun deleteAll() {
            activities.value = emptyList()
        }

        override suspend fun getSyncInfo(): SyncInfoEntity? = syncInfo.value

        override fun observeSyncInfo(): Flow<SyncInfoEntity?> = syncInfo

        override suspend fun saveSyncInfo(info: SyncInfoEntity) {
            syncInfo.value = info
        }

        override suspend fun deleteSyncInfo() {
            syncInfo.value = null
        }
    }

    @Test
    fun noActivitiesNorRemindersStayOnTheDeviceAfterClearing() = runBlocking {
        val dao = FakeActivityDao()
        val reminders = FakeReminderScheduler()
        dao.insertAll(listOf(activity.toEntity()))
        dao.saveSyncInfo(SyncInfoEntity(lastUpdated = "18 Nov 12:00"))
        syncReminders(reminders, listOf(activity), "me", now)
        assertEquals(setOf("a"), reminders.active)

        removeDeviceData(dao, reminders)

        assertTrue(dao.activities.value.isEmpty())
        assertNull(dao.syncInfo.value)
        assertTrue(reminders.active.isEmpty())
    }

    @Test
    fun localRepositoryHasNoActivitiesAfterClearing() = runBlocking {
        val repository = LocalActivityRepository(initialActivities = listOf(activity))

        repository.clearLocalData()

        assertTrue(repository.activities.value.isEmpty())
    }
}
