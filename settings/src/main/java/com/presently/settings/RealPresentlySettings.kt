package com.presently.settings

import android.app.AlarmManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.presently.logging.AnalyticsLogger
import com.presently.settings.model.*
import java.util.*
import org.threeten.bp.LocalTime
import javax.inject.Inject

class RealPresentlySettings @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    private val analytics: AnalyticsLogger
) : PresentlySettings {

    override fun getCurrentTheme(): String {
        return sharedPrefs.getString(THEME_PREF, "original") ?: "original"
    }

    override fun setTheme(themeName: String) {
        sharedPrefs.edit()
            .putString(THEME_PREF, themeName)
            .apply()

        analytics.recordSelectEvent(themeName, "theme")
    }

    override fun isBiometricsEnabled(): Boolean {
        return sharedPrefs.getBoolean(FINGERPRINT, false)
    }

    override fun shouldLockApp(): Boolean {
        val lastDestroyTime = sharedPrefs.getLong(ON_PAUSE_TIME, -1L)
        val currentTime = Date(System.currentTimeMillis()).time
        val diff = currentTime - lastDestroyTime
        //if more than 5 minutes (300000ms) have passed since last destroy, lock out user
        return diff > 300000L
    }

    override fun setOnPauseTime() {
        val date = Date(System.currentTimeMillis())
        sharedPrefs.edit().putLong(ON_PAUSE_TIME, date.time).apply()
    }

    override fun getFirstDayOfWeek(): Int {
        return when (sharedPrefs.getString(FIRST_DAY_OF_WEEK, "monday")) {
            "0" -> Calendar.SATURDAY
            "1" -> Calendar.SUNDAY
            else -> Calendar.MONDAY
        }
    }

    override fun shouldShowQuote(): Boolean {
        return sharedPrefs.getBoolean(SHOW_QUOTE, true)
    }

    override fun getAutomaticBackupCadence(): BackupCadence {
        return when (sharedPrefs.getString(BACKUP_CADENCE, "0") ?: "0") {
            "0" -> BackupCadence.DAILY
            "1" -> BackupCadence.WEEKLY
            else -> BackupCadence.EVERY_CHANGE
        }
    }

    override fun getLocale(): String {
        val languagePref = sharedPrefs.getString(APP_LANGUAGE, NO_LANG_PREF) ?: NO_LANG_PREF
        return if (languagePref == NO_LANG_PREF) {
            getDeviceLanguage()
        } else {
            languagePref
        }
    }

    override fun hasEnabledNotifications(): Boolean {
        return sharedPrefs.getBoolean(NOTIFS, true)
    }

    override fun getNotificationTime(): LocalTime {
        val prefTime = sharedPrefs.getString(NOTIF_PREF_TIME, "21:00")
        return LocalTime.parse(prefTime)
    }

    override fun hasUserDisabledAlarmReminders(context: Context): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //the user is on 12+ so we have to check if we still have permission
            !alarmManager.canScheduleExactAlarms()
        } else {
            //a pre 12 user cannot disable exact alarms
            false
        }
    }

    override fun getLinesPerEntryInTimeline(): Int {
        return sharedPrefs.getInt(LINES_PER_ENTRY_IN_TIMELINE, 10)
    }

    override fun shouldShowDayOfWeekInTimeline(): Boolean {
        return sharedPrefs.getBoolean(DAY_OF_WEEK, false)
    }

    @Deprecated("Dropbox support removed")
    override fun getAccessToken(): Nothing? {
        return null
    }

    @Deprecated("Dropbox support removed")
    override fun setAccessToken(newToken: Nothing) {
        // No-op: Dropbox support removed
    }

    @Deprecated("Dropbox support removed")
    override fun markDropboxAuthInitiated() {
        // No-op: Dropbox support removed
    }

    @Deprecated("Dropbox support removed")
    override fun wasDropboxAuthInitiated(): Boolean {
        return false
    }

    @Deprecated("Dropbox support removed")
    override fun markDropboxAuthAsCancelled() {
        // No-op: Dropbox support removed
    }

    @Deprecated("Dropbox support removed")
    override fun clearAccessToken() {
        // No-op: Dropbox support removed
    }

    override fun isOptedIntoAnalytics(): Boolean {
        return sharedPrefs.getBoolean(ANALYTICS_OPT_IN_PREF, true)
    }

    private fun getDeviceLanguage(): String {
        return Locale.getDefault().toLanguageTag()
    }

}