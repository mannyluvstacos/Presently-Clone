package com.presently.settings

import android.content.Context
import org.threeten.bp.LocalTime

interface PresentlySettings {

    fun getCurrentTheme(): String

    fun setTheme(themeName: String)

    fun isBiometricsEnabled(): Boolean

    fun shouldLockApp(): Boolean

    fun setOnPauseTime()

    fun getFirstDayOfWeek(): Int

    fun shouldShowQuote(): Boolean

    fun getAutomaticBackupCadence(): BackupCadence

    fun getLocale(): String

    fun hasEnabledNotifications(): Boolean

    fun getNotificationTime(): LocalTime

    fun hasUserDisabledAlarmReminders(context: Context): Boolean

    fun getLinesPerEntryInTimeline(): Int

    fun shouldShowDayOfWeekInTimeline(): Boolean

    @Deprecated("Dropbox support removed")
    fun getAccessToken(): Any?

    @Deprecated("Dropbox support removed")
    fun setAccessToken(newToken: Any?)

    @Deprecated("Dropbox support removed")
    fun wasDropboxAuthInitiated(): Boolean

    @Deprecated("Dropbox support removed")
    fun markDropboxAuthAsCancelled()

    @Deprecated("Dropbox support removed")
    fun markDropboxAuthInitiated()

    @Deprecated("Dropbox support removed")
    fun clearAccessToken()

    fun isOptedIntoAnalytics(): Boolean
}

enum class BackupCadence(val index: Int, val string: String) {
    DAILY(0, "Daily"),
    WEEKLY(1, "Weekly"),
    EVERY_CHANGE(2, "Every change")
}