package journal.gratitude.com.gratitudejournal.fakes

import android.content.Context
import journal.gratitude.com.gratitudejournal.model.CloudUploadResult
import journal.gratitude.com.gratitudejournal.model.Entry
import journal.gratitude.com.gratitudejournal.model.UploadSuccess
import journal.gratitude.com.gratitudejournal.util.backups.LocalBackupProvider
import javax.inject.Inject

class FakeLocalBackupProvider @Inject constructor(context: Context):
    LocalBackupProvider(context) {
    override suspend fun createBackup(entries: List<Entry>): CloudUploadResult {
        return UploadSuccess
    }
}