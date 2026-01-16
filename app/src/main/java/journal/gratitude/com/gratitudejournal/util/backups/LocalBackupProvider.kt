package journal.gratitude.com.gratitudejournal.util.backups

import android.content.Context
import journal.gratitude.com.gratitudejournal.model.CloudUploadResult
import journal.gratitude.com.gratitudejournal.model.Entry
import journal.gratitude.com.gratitudejournal.model.UploadError
import journal.gratitude.com.gratitudejournal.model.UploadSuccess
import journal.gratitude.com.gratitudejournal.util.toDatabaseString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * A backup provider that saves backups to a local "backups" folder in markdown format.
 * Each backup is named with a timestamp to ensure uniqueness.
 */
class LocalBackupProvider(private val context: Context) {

    suspend fun createBackup(entries: List<Entry>): CloudUploadResult {
        return withContext(Dispatchers.IO) {
            try {
                // Create backups folder if it doesn't exist
                val backupsDir = File(context.getExternalFilesDir(null), "backups")
                if (!backupsDir.exists()) {
                    backupsDir.mkdirs()
                }

                // Generate filename with timestamp
                val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
                val filename = "presently-backup_$timestamp.md"
                val backupFile = File(backupsDir, filename)

                // Write entries in markdown format
                FileWriter(backupFile).use { writer ->
                    writer.write("# Presently Backup\n\n")
                    writer.write("Backup created: ${LocalDateTime.now()}\n\n")
                    writer.write("---\n\n")

                    for (entry in entries.sortedByDescending { it.entryDate }) {
                        writer.write("## ${entry.entryDate.toDatabaseString()}\n\n")
                        writer.write("${entry.entryContent}\n\n")
                        writer.write("---\n\n")
                    }
                }

                UploadSuccess
            } catch (e: IOException) {
                UploadError(e)
            } catch (e: Exception) {
                UploadError(e)
            }
        }
    }
}
