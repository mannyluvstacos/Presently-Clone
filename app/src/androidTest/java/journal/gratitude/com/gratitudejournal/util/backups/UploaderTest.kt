package journal.gratitude.com.gratitudejournal.util.backups

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import com.google.common.truth.Truth.assertThat
import com.presently.coroutine_utils.AppCoroutineDispatchers
import com.presently.logging.AnalyticsLogger
import com.presently.logging.CrashReporter
import com.presently.settings.BackupCadence
import com.presently.settings.PresentlySettings
import journal.gratitude.com.gratitudejournal.model.CloudUploadResult
import journal.gratitude.com.gratitudejournal.model.Entry
import journal.gratitude.com.gratitudejournal.model.UploadError
import journal.gratitude.com.gratitudejournal.model.UploadSuccess
import journal.gratitude.com.gratitudejournal.repository.EntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.lang.Exception
import kotlin.test.fail

class UploaderTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val dispatchers = AppCoroutineDispatchers(
        io = TestCoroutineDispatcher(),
        computation = TestCoroutineDispatcher(),
        main = TestCoroutineDispatcher()
    )

    private val repo = object : EntryRepository {
        override suspend fun getEntries(): List<Entry> {
            return listOf(Entry(LocalDate.of(2021, 12, 25), "Merry Christmas!"))
        }

        override suspend fun getEntry(date: LocalDate): Entry = fail("Not needed in this test")
        override suspend fun getEntriesFlow(): Flow<List<Entry>> = fail("Not needed in this test")
        override fun getWrittenDates(): LiveData<List<LocalDate>> = fail("Not needed in this test")
        override suspend fun addEntry(entry: Entry) = fail("Not needed in this test")
        override suspend fun addEntries(entries: List<Entry>) = fail("Not needed in this test")
        override fun searchEntries(query: String): Flow<PagingData<Entry>> =
            fail("Not needed in this test")
    }

    private var wasBackupProviderCalled = false

    private val backupProvider = object : LocalBackupProvider(context) {
        override suspend fun createBackup(entries: List<Entry>): CloudUploadResult {
            wasBackupProviderCalled = true
            return UploadSuccess
        }
    }

    private val crashReporter = object : CrashReporter {
        var loggedException: Exception? = null
        override fun logHandledException(exception: Exception) {
            loggedException = exception
        }

        override fun optOutOfCrashReporting() = fail("Not needed in this test")
        override fun optIntoCrashReporting() = fail("Not needed in this test")
    }

    private val settings = object : PresentlySettings {
        override fun getAccessToken(): Nothing? = null
        override fun clearAccessToken() = fail("Not needed in this test")
        override fun setAccessToken(newToken: Nothing) = fail("Not needed in this test")
        override fun getCurrentTheme(): String = fail("Not needed in this test")
        override fun setTheme(themeName: String) = fail("Not needed in this test")
        override fun isBiometricsEnabled(): Boolean = fail("Not needed in this test")
        override fun shouldLockApp(): Boolean = fail("Not needed in this test")
        override fun setOnPauseTime() = fail("Not needed in this test")
        override fun getFirstDayOfWeek(): Int = fail("Not needed in this test")
        override fun shouldShowQuote(): Boolean = fail("Not needed in this test")
        override fun getAutomaticBackupCadence(): BackupCadence = fail("Not needed in this test")
        override fun getLocale(): String = fail("Not needed in this test")
        override fun hasEnabledNotifications(): Boolean = fail("Not needed in this test")
        override fun getNotificationTime(): LocalTime = fail("Not needed in this test")
        override fun hasUserDisabledAlarmReminders(context: Context): Boolean = fail("Not needed in this test")
        override fun getLinesPerEntryInTimeline(): Int = fail("Not needed in this test")
        override fun shouldShowDayOfWeekInTimeline(): Boolean = fail("Not needed in this test")
        override fun wasDropboxAuthInitiated(): Boolean = fail("Not needed in this test")
        override fun markDropboxAuthAsCancelled() = fail("Not needed in this test")
        override fun markDropboxAuthInitiated() = fail("Not needed in this test")
        override fun isOptedIntoAnalytics(): Boolean = fail("Not needed in this test")
    }

    private val analytics = object : AnalyticsLogger {
        override fun recordEvent(event: String) = fail("Not needed in this test")
        override fun recordEvent(event: String, details: Map<String, Any>) =
            fail("Not needed in this test")
        override fun recordSelectEvent(selectedContent: String, selectedContentType: String) =
            fail("Not needed in this test")
        override fun recordEntryAdded(numEntries: Int) = fail("Not needed in this test")
        override fun recordView(viewName: String) = fail("Not needed in this test")
        override fun optOutOfAnalytics() = fail("Not needed in this test")
        override fun optIntoAnalytics() = fail("Not needed in this test")
    }

    @Test
    fun emptyRepositoryDoesNothing() = runBlockingTest {
        wasBackupProviderCalled = false
        val repo = object : EntryRepository {
            override suspend fun getEntries(): List<Entry> {
                return emptyList()
            }
            override suspend fun getEntry(date: LocalDate): Entry = fail("Not needed in this test")
            override suspend fun getEntriesFlow(): Flow<List<Entry>> = fail("Not needed in this test")
            override fun getWrittenDates(): LiveData<List<LocalDate>> = fail("Not needed in this test")
            override suspend fun addEntry(entry: Entry) = fail("Not needed in this test")
            override suspend fun addEntries(entries: List<Entry>) = fail("Not needed in this test")
            override fun searchEntries(query: String): Flow<PagingData<Entry>> =
                fail("Not needed in this test")
        }

        val uploader = RealUploader(dispatchers, repo, backupProvider, crashReporter, settings)

        val actual = uploader.uploadEntries(context)

        assertThat(actual).isEqualTo(ListenableWorker.Result.success())
        assertThat(wasBackupProviderCalled).isFalse() //don't use the backup provider here since there is no data to upload
    }

    @Test
    fun successfulBackup() = runBlockingTest {
        wasBackupProviderCalled = false
        val uploader = RealUploader(dispatchers, repo, backupProvider, crashReporter, settings)
        val actual = uploader.uploadEntries(context)

        assertThat(actual).isEqualTo(ListenableWorker.Result.success())
        assertThat(wasBackupProviderCalled).isTrue()
    }

    @Test
    fun failedBackup() = runBlockingTest {
        crashReporter.loggedException = null
        val exception = Exception("Backup failed")
        val failingBackupProvider = object : LocalBackupProvider(context) {
            override suspend fun createBackup(entries: List<Entry>): CloudUploadResult {
                return UploadError(exception)
            }
        }

        val uploader = RealUploader(dispatchers, repo, failingBackupProvider, crashReporter, settings)
        val actual = uploader.uploadEntries(context)

        assertThat(actual).isEqualTo(ListenableWorker.Result.failure())
        assertThat(crashReporter.loggedException).isEqualTo(exception) //log the exception
    }
}