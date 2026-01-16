package journal.gratitude.com.gratitudejournal.ui

import android.content.Context
import com.presently.settings.BackupCadence
import com.presently.settings.PresentlySettings
import com.presently.settings.wiring.PresentlySettingsModule
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.threeten.bp.LocalTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PresentlySettings binding to use in tests.
 *
 * Hilt will inject a [FakePresentlySettings] instead of a [RealPresentlySettings].
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [PresentlySettingsModule::class]
)
abstract class FakeSettingsModule {
    @Singleton
    @Binds
    abstract fun bindSettings(repo: FakePresentlySettings): PresentlySettings
}

class FakePresentlySettings @Inject constructor(): PresentlySettings {
    override fun getCurrentTheme(): String {
        return "Original"
    }

    override fun setTheme(themeName: String) {}

    override fun isBiometricsEnabled(): Boolean {
        return false
    }

    override fun shouldLockApp(): Boolean {
        return false
    }

    override fun setOnPauseTime() {}

    override fun getFirstDayOfWeek(): Int {
        return Calendar.MONDAY
    }

    override fun shouldShowQuote(): Boolean {
        return true
    }

    override fun getAutomaticBackupCadence(): BackupCadence {
        return BackupCadence.DAILY
    }

    override fun getLocale(): String {
        return "en-US"
    }

    override fun hasEnabledNotifications(): Boolean {
        return true
    }

    override fun getNotificationTime(): LocalTime {
        return LocalTime.parse("21:00")
    }

    override fun hasUserDisabledAlarmReminders(context: Context): Boolean {
        return false
    }

    override fun getLinesPerEntryInTimeline(): Int {
        return 10
    }

    override fun shouldShowDayOfWeekInTimeline(): Boolean {
        return false
    }

    @Deprecated("Dropbox support removed")
    override fun getAccessToken(): Nothing? {
        return null
    }

    @Deprecated("Dropbox support removed")
    override fun setAccessToken(newToken: Nothing) {}

    @Deprecated("Dropbox support removed")
    override fun wasDropboxAuthInitiated(): Boolean = false

    @Deprecated("Dropbox support removed")
    override fun markDropboxAuthAsCancelled() {}

    @Deprecated("Dropbox support removed")
    override fun markDropboxAuthInitiated() {}

    @Deprecated("Dropbox support removed")
    override fun clearAccessToken() { }

    override fun isOptedIntoAnalytics(): Boolean = true
}