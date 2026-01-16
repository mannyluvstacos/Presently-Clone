package journal.gratitude.com.gratitudejournal.util.backups

import android.content.Context
import androidx.work.ListenableWorker
import com.presently.coroutine_utils.AppCoroutineDispatchers
import com.presently.logging.CrashReporter
import com.presently.settings.PresentlySettings
import journal.gratitude.com.gratitudejournal.model.*
import journal.gratitude.com.gratitudejournal.repository.EntryRepository
import javax.inject.Inject

/**
 * A class that will handle the backup to local storage. It saves backups in markdown format
 * with unique filenames based on timestamps.
 */
class RealUploader @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val repository: EntryRepository,
    private val backupProvider: LocalBackupProvider,
    private val crashReporter: CrashReporter,
    private val settings: PresentlySettings
) : Uploader {
    override suspend fun uploadEntries(appContext: Context): ListenableWorker.Result {
        //get items
        val items = repository.getEntries()

        //do not backup data if there is nothing to back up
        if (items.isEmpty()) {
            return ListenableWorker.Result.success()
        }

        // Create local backup
        return when (val result = backupProvider.createBackup(items)) {
            is UploadError -> {
                crashReporter.logHandledException(result.exception)
                ListenableWorker.Result.failure()
            }
            is UploadSuccess -> ListenableWorker.Result.success()
        }
    }
}

interface Uploader {
    suspend fun uploadEntries(appContext: Context): ListenableWorker.Result
}